package audio.rabid.vinylscrobbler.ui.addalbum.search

import audio.rabid.kaddi.dsl.bind
import audio.rabid.kaddi.dsl.module
import audio.rabid.kaddi.dsl.require
import audio.rabid.kaddi.dsl.withSingleton
import audio.rabid.kaddi.instance
import audio.rabid.vinylscrobbler.data.musicbrainz.MusicBrainzApi
import com.fixdapp.android.logger.Logger

val SearchReleaseGroupsModule = module("SearchReleaseGroups") {

    require<MusicBrainzApi>()
    require<Logger>()
    bind<SearchReleaseGroupsViewModel>().withSingleton {
        SearchReleaseGroupsViewModel(
            musicBrainzApi = instance(),
            logger = instance()
        )
    }
}
