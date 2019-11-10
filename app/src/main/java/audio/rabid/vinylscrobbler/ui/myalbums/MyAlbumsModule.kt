package audio.rabid.vinylscrobbler.ui.myalbums

import audio.rabid.vinylscrobbler.core.ViewModel
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import javax.inject.Provider

val MyAlbumsModule = module {

    bind<MyAlbumsViewModel>().toProvider()
}

class ViewModelProvider<VM : ViewModel> : Provider<VM> {

    override fun get(): VM {

    }
}
