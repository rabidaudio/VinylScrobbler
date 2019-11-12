package audio.rabid.vinylscrobbler

import audio.rabid.vinylscrobbler.data.AppDatabase
import audio.rabid.vinylscrobbler.data.lastfm.LastFMApi
import dagger.Component
import dagger.android.AndroidInjectionModule

@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class
    ]
)
interface AppComponent {

    // A module is a list of binding implementations (when @Inject constructor doesn't work).
    // A component is a way to only expose specific bindings

    fun database(): AppDatabase

    fun lastFMApi(): LastFMApi
}
