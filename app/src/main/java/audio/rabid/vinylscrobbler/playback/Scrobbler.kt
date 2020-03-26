package audio.rabid.vinylscrobbler.playback

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import audio.rabid.vinylscrobbler.playback.ScrobblerService.Companion.NOTIFICATION_CHANNEL_NAME
import audio.rabid.vinylscrobbler.data.db.AppDatabase
import audio.rabid.vinylscrobbler.data.db.models.Track
import audio.rabid.vinylscrobbler.data.lastfm.LastFMApi
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class Scrobbler(
    private val applicationContext: Context,
    private val appDatabase: AppDatabase,
    private val lastFMApi: LastFMApi,
    private val notificationManager: NotificationManager
) {

    companion object {
        const val TIME_STEP = 500L
    }

    private val playbackJob = Job()

    @Parcelize
    data class PlayRequest(
//        val albumId: Long,
        val trackIds: List<Long>,
        val lastFmUsername: String,
        val lastFmPassword: String
    ): Parcelable

    fun play(playRequest: PlayRequest, notificationId: Int) {
        CoroutineScope(Dispatchers.Main + playbackJob).launch {
            // TODO these can be in parallel
            lastFMApi.authenticate(playRequest.lastFmUsername, playRequest.lastFmPassword)
            val tracks = appDatabase.trackDao().find(playRequest.trackIds)
            for (track in tracks) {
                val startTime = Instant.now()
                val endTime = startTime + track.duration
                do {
                    // update notification
                    val currentDuration = Duration.ofSeconds(startTime.until(Instant.now(), ChronoUnit.SECONDS))
                    notificationManager.notify(notificationId, getNotification(track, currentDuration))
                    delay(TIME_STEP)
                } while (Instant.now() < endTime)
                // TODO this can run in parallel while the next track starts playing
                lastFMApi.scrobble(
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
    }

    fun stop() {
        playbackJob.cancel()
    }

    private fun getNotification(track: Track, duration: Duration): Notification {
        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_NAME).apply {
            // TODO: style notification
        }.build()
    }
}
