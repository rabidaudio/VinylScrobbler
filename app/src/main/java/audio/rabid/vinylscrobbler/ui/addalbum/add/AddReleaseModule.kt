package audio.rabid.vinylscrobbler.ui.addalbum.add

import audio.rabid.kaddi.dsl.bind
import audio.rabid.kaddi.dsl.module
import audio.rabid.kaddi.dsl.require
import audio.rabid.kaddi.dsl.withSingleton
import audio.rabid.kaddi.instance
import audio.rabid.vinylscrobbler.data.musicbrainz.MusicBrainzApi
import com.fixdapp.android.logger.Logger

val AddReleaseModule = module("AddRelease") {

    require<MusicBrainzApi>()
    require<Logger>()
    require<AddReleaseActivity.Arguments>()
    bind<AddReleaseViewModel>().withSingleton {
        AddReleaseViewModel(
            arguments = instance(),
            musicBrainzApi = instance()
        )
    }
}
