package audio.rabid.vinylscrobbler.ui.addalbum

import audio.rabid.kaddi.dsl.bind
import audio.rabid.kaddi.dsl.module
import audio.rabid.kaddi.dsl.withSingleton

val AddAlbumModule = module("AddAlbum") {

    bind<AddAlbumViewModel>().withSingleton {
        AddAlbumViewModel()
    }
}
