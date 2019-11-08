package audio.rabid.vinylscrobbler.lastfm

import audio.rabid.vinylscrobbler.BuildConfig
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module

val LastFMModule = module {
    bind<String>().withName("LASTFM_API_KEY").toInstance("1bffd9f5cbbad9d38d4b914810959677")
    bind<String>().withName("LASTFM_API_SECRET").toInstance("29df28e7e68d0ca183d40fdc47475c33")
    bind<String>().withName("USER_AGENT").toInstance {
        "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME} ${BuildConfig.CONTACT_EMAIL}"
    }

    bind<LastFMAuthenticationInterceptor>().singleton()

    bind<LastFMSessionManager>().toClass<LastFMAuthenticationInterceptor>()

    bind<LastFMApi>().toProvider(LastFMApi.Factory::class).providesSingleton()
}
