package audio.rabid.vinylscrobbler

import android.content.Context
import audio.rabid.kaddi.dsl.*
import audio.rabid.kaddi.instance
import audio.rabid.kaddi.setInstance
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.core.FlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader

val FlipperModule = module("Flipper") {

    require<Context>()

    declareSetBinding<FlipperPlugin>()

    bindIntoSet<FlipperPlugin>().with {
        InspectorFlipperPlugin(instance(), DescriptorMapping.withDefaults())
    }

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
    }
}
