package audio.rabid.vinylscrobbler.ui.myalbums

import audio.rabid.kaddi.dsl.*
import audio.rabid.vinylscrobbler.data.AppDatabase


val MyAlbumsModule = module("MyAlbums") {

    require<AppDatabase>()
    bind<MyAlbumsViewModel>().withSingleton {
        MyAlbumsViewModel(appDatabase = instance())
    }
}
