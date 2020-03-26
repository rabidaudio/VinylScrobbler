package audio.rabid.vinylscrobbler.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import audio.rabid.kaddi.Kaddi
import audio.rabid.kaddi.inject
import audio.rabid.vinylscrobbler.R

class ScrobblerService : Service() {

    companion object {
        const val NOTIFICATION_CHANNEL_NAME = "Playback"
        private const val EXTRA_PLAY_REQUEST = "EXTRA_PLAY_REQUEST"

        fun beginPlayback(context: Context, playRequest: Scrobbler.PlayRequest) {
            context.startForegroundService(Intent(context, ScrobblerService::class.java).apply {
                putExtra(EXTRA_PLAY_REQUEST, playRequest)
            })
        }
    }

    private val scrobbler: Scrobbler by inject()

    override fun onCreate() {
        super.onCreate()
        Kaddi.getScope(application)
            .createChildScope(this, PlaybackModule)
            .inject(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }
        val playRequest = intent.getParcelableExtra<Scrobbler.PlayRequest>(EXTRA_PLAY_REQUEST)
        if (playRequest == null) {
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }
        val notificationId = 1
        startForeground(notificationId, getForegroundNotification())
        scrobbler.play(playRequest, notificationId)
        return START_STICKY
    }

    override fun onDestroy() {
        scrobbler.stop()
        Kaddi.closeScope(this)
        super.onDestroy()
    }

    private fun getForegroundNotification(): Notification {
        val notificationService = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationService.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_NAME,
                getString(R.string.playback_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_NAME).apply {
            // TODO: style placeholder notification
            setContentText("Setting up...")
        }.build()
    }
}
