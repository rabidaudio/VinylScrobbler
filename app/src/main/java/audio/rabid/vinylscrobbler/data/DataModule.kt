package audio.rabid.vinylscrobbler.data

import android.content.Context
import audio.rabid.kaddi.dsl.*
import audio.rabid.vinylscrobbler.core.adapters.HttpUrlAdapter
import audio.rabid.vinylscrobbler.core.adapters.InstantAdapter
import audio.rabid.vinylscrobbler.core.adapters.UUIDAdapter
import audio.rabid.vinylscrobbler.core.adapters.WrappedAdapterFactory
import audio.rabid.vinylscrobbler.data.lastfm.LastFMModule

val DataModule = module("Data") {

    import(MoshiModule)
    import(RetrofitModule)
    import(LastFMModule)

    // common adapters
    bindIntoSet<Any>("MoshiAdapters").with { WrappedAdapterFactory }
    bindIntoSet<Any>("MoshiAdapters").with { HttpUrlAdapter() }
    bindIntoSet<Any>("MoshiAdapters").with { UUIDAdapter() }
    bindIntoSet<Any>("MoshiAdapters").with { InstantAdapter() }

    require<Context>()
    bind<AppDatabase>().withSingleton {
        AppDatabase.get(applicationContext = instance())
    }
}
