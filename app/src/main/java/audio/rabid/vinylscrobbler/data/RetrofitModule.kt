package audio.rabid.vinylscrobbler.data

import audio.rabid.kaddi.dsl.*
import audio.rabid.vinylscrobbler.core.adapters.JSONObjectConverterFactory
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val RetrofitModule = module("Retrofit") {
    import(MoshiModule)

    declareSetBinding<Interceptor>()

    bind<OkHttpClient>().with {
        OkHttpClient.Builder().apply {
            for (interceptor in setInstance<Interceptor>()) {
                addInterceptor(interceptor)
            }
        }.build()
    }

    require<Moshi>()
    bind<Retrofit.Builder>().with {
        Retrofit.Builder()
            .addConverterFactory(JSONObjectConverterFactory)
            .addConverterFactory(MoshiConverterFactory.create(instance()))
            .client(instance())
    }
}
