package audio.rabid.vinylscrobbler.data.lastfm

import audio.rabid.kaddi.dsl.*
import audio.rabid.kaddi.instance
import audio.rabid.vinylscrobbler.BuildConfig
import audio.rabid.vinylscrobbler.core.adapters.IntegerBoolean
import audio.rabid.vinylscrobbler.data.LoggingModule
import audio.rabid.vinylscrobbler.data.MoshiModule
import audio.rabid.vinylscrobbler.data.RetrofitModule
import com.fixdapp.android.logger.Logger
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create

val LastFMModule = module("LastFM") {
    import(RetrofitModule)
    import(MoshiModule)

    bindIntoSet<Any>("MoshiAdapters").with { IntegerBoolean.Adapter() }

    bindConstant("LASTFM_API_KEY") { "1bffd9f5cbbad9d38d4b914810959677" }
    bindConstant("LASTFM_API_SECRET") { "29df28e7e68d0ca183d40fdc47475c33" }
    bindConstant("USER_AGENT") {
        "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME} ${BuildConfig.CONTACT_EMAIL}"
    }

    bind<LastFMAuthenticationInterceptor>().withSingleton {
        LastFMAuthenticationInterceptor(
            apiKey = instance("LASTFM_API_KEY"),
            apiSecret = instance("LASTFM_API_SECRET"),
            userAgent = instance("USER_AGENT")
        )
    }

    bind<LastFMSessionManager>().with { instance<LastFMAuthenticationInterceptor>() }

    require<Retrofit.Builder>()
    require<OkHttpClient>()
    bind<LastFMApi>().withSingleton {
        instance<Retrofit.Builder>()
            .baseUrl(LastFMApi.BASE_URL)
            .client(instance<OkHttpClient>().newBuilder()
                .addInterceptor(instance<LastFMAuthenticationInterceptor>())
                .build())
            .build()
            .create()
    }

    import(LoggingModule)
    require<Logger>()
    bind<LastFMApi>(overrides = true).with {
        MockLastFMApi(logger = instance())
    }
}
