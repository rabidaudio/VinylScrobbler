package audio.rabid.vinylscrobbler

import android.app.Application
import android.content.Context
import audio.rabid.kaddi.dsl.*
import audio.rabid.kaddi.instance
import audio.rabid.kaddi.setInstance
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.core.FlipperPlugin
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.leakcanary.LeakCanaryFlipperPlugin
import com.facebook.flipper.plugins.leakcanary.RecordLeakService
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import okhttp3.Interceptor

val FlipperModule = module("Flipper") {

    require<Context>()

    declareSetBinding<FlipperPlugin>()

    bindIntoSet<FlipperPlugin>().with {
        InspectorFlipperPlugin(instance(), DescriptorMapping.withDefaults())
    }

    bind<NetworkFlipperPlugin>().withSingleton { NetworkFlipperPlugin() }
    bindIntoSet<FlipperPlugin>().with { instance<NetworkFlipperPlugin>() }
    bindIntoSet<Interceptor>().with { FlipperOkhttpInterceptor(instance()) }

    bindIntoSet<FlipperPlugin>().with { DatabasesFlipperPlugin(instance<Context>()) }

//    bindIntoSet<FlipperPlugin>().with { LeakCanaryFlipperPlugin() }

    bindIntoSet<FlipperPlugin>().with { CrashReporterPlugin.getInstance() }

    bind<FlipperClient>().with {
        AndroidFlipperClient.getInstance(instance()).apply {
            for (plugin in setInstance<FlipperPlugin>()) addPlugin(plugin)
        }
    }

    onAttachedToScope {
        val context = instance<Context>()
        SoLoader.init(context, false)
        if (FlipperUtils.shouldEnableFlipper(context)) {
            instance<FlipperClient>().start()
        }
        if (!LeakCanary.isInAnalyzerProcess(context)
            && LeakCanary.installedRefWatcher() == RefWatcher.DISABLED) {
            LeakCanary.install(context as Application)
//            LeakCanary.refWatcher(context)
//                .listenerServiceClass(RecordLeakService::class.java)
//                .buildAndInstall()
        }
    }
}
