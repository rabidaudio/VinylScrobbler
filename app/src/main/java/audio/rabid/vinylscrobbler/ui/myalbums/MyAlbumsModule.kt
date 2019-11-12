package audio.rabid.vinylscrobbler.ui.myalbums

import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dagger.Binds



@Module //(subcomponents = [MyAlbumsActivity.Compontent::class])
abstract class MyAlbumsModule {

//    @Binds
//    @IntoMap
//    @ClassKey(MyAlbumsActivity::class)
//    internal abstract fun bindYourAndroidInjectorFactory(factory: MyAlbumsActivity.Compontent.Factory): AndroidInjector.Factory<*>

}

