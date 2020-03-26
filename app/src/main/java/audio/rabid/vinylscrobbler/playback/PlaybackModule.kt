package audio.rabid.vinylscrobbler.playback

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import audio.rabid.kaddi.dsl.bind
import audio.rabid.kaddi.dsl.module
import audio.rabid.kaddi.dsl.require
import audio.rabid.kaddi.dsl.with
import audio.rabid.kaddi.instance
import audio.rabid.vinylscrobbler.data.DataModule

val PlaybackModule = module("Playback") {
    import(DataModule)

    require<Context>(Application::class)
    bind<Scrobbler>().with {
        Scrobbler(
            applicationContext = instance(Application::class),
            appDatabase = instance(),
            lastFMApi = instance(),
            notificationManager = instance()
        )
    }

    // TODO: might be cool to have private bindings. They are only available to the module
    // they are declared in
    bind<NotificationManager>().with {
        instance<Context>(Application::class)
            .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }
}
