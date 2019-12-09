package audio.rabid.vinylscrobbler.data

import audio.rabid.kaddi.dsl.*
import audio.rabid.kaddi.setInstance
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

val MoshiModule = module("Moshi") {

    declareSetBinding<Any>("MoshiAdapters")

    bind<Moshi>().withSingleton {
        Moshi.Builder().apply {
            for (adapter in setInstance<Any>("MoshiAdapters")) {
                if (adapter is JsonAdapter.Factory) {
                    add(adapter)
                } else {
                    add(adapter)
                }
            }
        }.build()
    }
}
