package audio.rabid.vinylscrobbler.playback

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Parcelable
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat as MediaNotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.media.session.MediaButtonReceiver
import audio.rabid.vinylscrobbler.R
import audio.rabid.vinylscrobbler.playback.ScrobblerService.Companion.NOTIFICATION_CHANNEL_NAME
import audio.rabid.vinylscrobbler.data.db.AppDatabase
import audio.rabid.vinylscrobbler.data.db.models.Album
import audio.rabid.vinylscrobbler.data.db.models.Track
import audio.rabid.vinylscrobbler.data.lastfm.LastFMApi
import audio.rabid.vinylscrobbler.playback.Scrobbler.Update.Type.*
import audio.rabid.vinylscrobbler.playback.ScrobblerService.Companion.FOREGROUND_NOTIFICATION_ID
import audio.rabid.vinylscrobbler.ui.coverImageLoader
import audio.rabid.vinylscrobbler.ui.myalbums.MyAlbumsActivity
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.consumeEach
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.SECONDS

@OptIn(ExperimentalCoroutinesApi::class)
class Scrobbler(
    private val applicationContext: Context,
    private val appDatabase: AppDatabase,
    private val lastFMApi: LastFMApi,
    private val notificationManager: NotificationManager
) {

    companion object {
        const val TIME_STEP = 500L
    }

    private data class Update(
        val type: Type,
        val track: Track,
        val startTime: Instant,
        val currentDuration: Duration
    ) {
        enum class Type { STARTED, UPDATED, FINISHED }
    }

    private val playbackJob = Job()
    private val updateStream = Channel<Update>(UNLIMITED)

    @Parcelize
    data class PlayRequest(
//        val albumId: Long,
        val trackIds: List<Long>,
        val lastFmUsername: String,
        val lastFmPassword: String
    ): Parcelable

    fun play(playRequest: PlayRequest) {
        CoroutineScope(Dispatchers.Main + playbackJob).launch {
            updateStream.consumeEach { handleUpdate(it) }
        }
        CoroutineScope(Dispatchers.Main + playbackJob).launch {
            lastFMApi.authenticate(playRequest.lastFmUsername, playRequest.lastFmPassword)
            val tracks = appDatabase.trackDao().find(playRequest.trackIds)
            for (track in tracks) {
                val startTime = Instant.now()
                val endTime = startTime + track.duration
                postUpdate(Update(STARTED, track, startTime, Duration.ZERO))
                do {
                    // update notification
                    val currentDuration = startTime.secondsTo(Instant.now())
                    postUpdate(Update(UPDATED, track, startTime, currentDuration))
                    delay(TIME_STEP)
                } while (Instant.now() < endTime)
                postUpdate(Update(FINISHED, track, startTime, track.duration))
            }
        }
    }

    // The decoupling of updates from the loop means that the api calls don't mess up the timing
    private suspend fun postUpdate(update: Update) {
        updateStream.send(update)
    }

    private suspend fun handleUpdate(update: Update) {
        val bitmap = withContext(Dispatchers.IO) {
            album.coverUrl.coverImageLoader().get()
        }
        val (type, track, startTime, currentDuration) = update
        val notification = getNotification(track, currentDuration, bitmap)
        notificationManager.notify(FOREGROUND_NOTIFICATION_ID, notification)
        when (type) {
            STARTED -> lastFMApi.updateNowPlaying(
                trackName = track.title,
                artistName = track.artistName,
                albumName = track.albumName,
                trackNumber = track.position,
                musicBrainzTrackId = track.musicBrainzTrackId,
                durationSeconds = track.duration.seconds
            )
            FINISHED -> lastFMApi.scrobble(
                trackName = track.title,
                artistName = track.artistName,
                albumName = track.albumName,
                timestamp = startTime,
                trackNumber = track.position,
                musicBrainzTrackId = track.musicBrainzTrackId,
                durationSeconds = track.duration.seconds
            )
        }
    }

    fun stop() {
        playbackJob.cancel()
    }

    private fun getNotification(track: Track, album: Album, duration: Duration, cover: Bitmap?): Notification {
        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_NAME).apply {
            // TODO: style notification
            setContentTitle("${track.artistName} - ${track.albumName}")
            setContentText(track.title)
            setSmallIcon(R.drawable.ic_notification)
            priority = PRIORITY_DEFAULT
            setVisibility(VISIBILITY_PUBLIC)
            if (cover != null) setLargeIcon(cover)

            // TODO: take to album, or send commands to service
            val pendingIntent = PendingIntent.getActivity(applicationContext, 0, Intent(applicationContext, MyAlbumsActivity::class.java), 0)
            addAction(R.drawable.ic_stop, applicationContext.getString(R.string.stop), pendingIntent)
            // TODO: only if additional side
            addAction(R.drawable.ic_flip, applicationContext.getString(R.string.flip_over), pendingIntent)

//            MediaButtonReceiver.buildMediaButtonPendingIntent(
//                context,
//                PlaybackStateCompat.ACTION_STOP
//            )
            val mediaStyle = MediaNotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1)
                .setMediaSession(mediaSession.getSessionToken())
                .setShowCancelButton(true)
                .setCancelButtonIntent(pendingIntent)
            setStyle(mediaStyle)

//            setContentIntent() open album details
            setAutoCancel(false)
        }.build()
    }

    private fun Instant.secondsTo(other: Instant): Duration {
        return Duration.ofSeconds(until(other, SECONDS))
    }
}
