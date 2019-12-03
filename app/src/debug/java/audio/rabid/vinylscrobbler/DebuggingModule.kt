package audio.rabid.vinylscrobbler

import android.content.Context
import audio.rabid.kaddi.dsl.*
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader

val DebuggingModule = module("Debugging") {

    // bindIntoSet<Interceptor>().with { OkReplayInterceptor() }

    bind<FlipperClient>().with {
        val context = instance<Context>()
        AndroidFlipperClient.getInstance(context).apply {
            addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))
        }
    }

    require<Context>()
    onReady {
        val context = instance<Context>()
        SoLoader.init(context, false)

        if (FlipperUtils.shouldEnableFlipper(context)) {
            instance<FlipperClient>().start()
        }
    }
}
