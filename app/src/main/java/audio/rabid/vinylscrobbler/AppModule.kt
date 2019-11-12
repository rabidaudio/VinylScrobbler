package audio.rabid.vinylscrobbler

import audio.rabid.vinylscrobbler.data.DataModule
import dagger.Module


@Module(includes = [DataModule::class])
class AppModule
