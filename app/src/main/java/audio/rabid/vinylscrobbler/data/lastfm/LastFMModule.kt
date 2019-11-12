package audio.rabid.vinylscrobbler.data.lastfm

import dagger.Binds
import dagger.Module
import javax.inject.Named
import audio.rabid.vinylscrobbler.core.adapters.*
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
object LastFMModule {

    @Binds
    @Named("LASTFM_API_KEY")
    @JvmStatic
    fun apiKey() = "1bffd9f5cbbad9d38d4b914810959677"

    @Binds
    @Named("LASTFM_API_SECRET")
    @JvmStatic
    fun apiSecret() = "29df28e7e68d0ca183d40fdc47475c33"

//    @Binds
//    @Named("USER_AGENT")
//    fun userAgent() = "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME} ${BuildConfig.CONTACT_EMAIL}"

    @Binds
    @Singleton
    @JvmStatic
    fun sessionManager(interceptor: LastFMAuthenticationInterceptor) = interceptor

    @Provides
    @IntoSet
    @Named("MoshiAdapters")
    @JvmStatic
    fun integerBooleanAdapter(): Any {
        return IntegerBoolean.Adapter()
    }

    @Binds
    @JvmStatic
    fun api(retrofitBuilder: Retrofit.Builder, client: OkHttpClient, interceptor: LastFMAuthenticationInterceptor): LastFMApi {
        return retrofitBuilder
            .baseUrl(LastFMApi.BASE_URL)
            .client(client.newBuilder()
                .addInterceptor(interceptor)
                .build())
            .build()
            .create()
    }
}

