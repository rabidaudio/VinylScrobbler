package audio.rabid.vinylscrobbler

import android.app.Application
import audio.rabid.kaddi.Kaddi
import audio.rabid.kaddi.dsl.includingInstance

class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        Kaddi.createScope(this,
            AppModule.includingInstance(applicationContext),
            DebuggingModule)
    }
}
