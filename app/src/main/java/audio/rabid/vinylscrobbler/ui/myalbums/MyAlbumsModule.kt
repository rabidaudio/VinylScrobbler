package audio.rabid.vinylscrobbler.ui.myalbums

import audio.rabid.kaddi.dsl.*
import audio.rabid.kaddi.instance
import audio.rabid.vinylscrobbler.data.db.AppDatabase


val MyAlbumsModule = module("MyAlbums") {

    require<AppDatabase>()
    bind<MyAlbumsViewModel>().withSingleton {
        MyAlbumsViewModel(appDatabase = instance())
    }
}
