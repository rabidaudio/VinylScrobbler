package audio.rabid.vinylscrobbler.data

import android.app.Application
import android.content.Context
import audio.rabid.kaddi.dsl.*
import audio.rabid.kaddi.instance
import audio.rabid.vinylscrobbler.core.adapters.*
import audio.rabid.vinylscrobbler.data.db.AppDatabase
import audio.rabid.vinylscrobbler.data.lastfm.LastFMModule
import audio.rabid.vinylscrobbler.data.musicbrainz.MusicBrainzModule

val DataModule = module("Data") {
    import(MoshiModule)
    import(RetrofitModule)
    import(LastFMModule)
    import(MusicBrainzModule)

    // common adapters
    bindIntoSet<Any>("MoshiAdapters").with { WrappedAdapterFactory }
    bindIntoSet<Any>("MoshiAdapters").with { HttpUrlAdapter() }
    bindIntoSet<Any>("MoshiAdapters").with { UUIDAdapter() }
    bindIntoSet<Any>("MoshiAdapters").with { InstantAdapter() }
    bindIntoSet<Any>("MoshiAdapters").with { LocalDateAdapter() }

    require<Context>(Application::class)
    bind<AppDatabase>().withSingleton {
        AppDatabase.get(applicationContext = instance(Application::class))
    }
}
