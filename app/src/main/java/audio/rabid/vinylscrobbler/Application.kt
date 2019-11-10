package audio.rabid.vinylscrobbler

import android.app.Application
import android.content.Context
import audio.rabid.vinylscrobbler.core.ApplicationScope
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import toothpick.ktp.KTP
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import toothpick.smoothie.module.SmoothieApplicationModule


class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        SoLoader.init(this, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.start()
        }

        KTP.openScope(this)
            .supportScopeAnnotation(ApplicationScope::class.java)
            .installModules(
                SmoothieApplicationModule(this),
                module { bind<Context>().toInstance(this@Application) },
                AppModule
            )
    }
}
