package audio.rabid.vinylscrobbler

import android.app.Application
import android.content.Context
import audio.rabid.kaddi.Kaddi
import audio.rabid.kaddi.dsl.bind
import audio.rabid.kaddi.dsl.module
import audio.rabid.kaddi.dsl.with

class VSApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Kaddi.createScope(this,
            module("ApplicationContext") {
                bind<Context>(Application::class).with { applicationContext }
            },
            AppModule,
            DebuggingModule
        )
    }
}
