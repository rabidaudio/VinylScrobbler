package audio.rabid.vinylscrobbler

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okreplay.OkReplayInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import toothpick.InjectConstructor
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import javax.inject.Provider

val AppModule = module {

//    bind<Moshi>().toInstance {
//        Moshi.Builder()
//                // Toothpick doesn't support set bindings yet:
//                // https://github.com/stephanenicolas/toothpick/issues/368
//            .add(WrappedAdapterFactory)
//            .add(HttpUrlAdapter)
//            .add(UUIDAdapter)
//            .add(InstantAdapter)
//            .build()
//    }
//
//    bind<Retrofit>().toInstance {
//        Retrofit.Builder()
//            .addConverterFactory(MoshiConverterFactory.create())
//            .build()
//    }

    bind<OkHttpClient>().toInstance {
        OkHttpClient.Builder()
//            .addInterceptor(OkReplayInterceptor())
            .build()
    }

    bind<Moshi>().toProvider(MoshiProvider::class)
    bind<Retrofit.Builder>().toProvider(RetrofitProvider::class)
}

@InjectConstructor
class MoshiProvider : Provider<Moshi> {
    override fun get(): Moshi {
        return         Moshi.Builder()
            // Toothpick doesn't support set bindings yet:
            // https://github.com/stephanenicolas/toothpick/issues/368
            .add(WrappedAdapterFactory)
            .add(HttpUrlAdapter())
            .add(UUIDAdapter())
            .add(InstantAdapter())
            .add(IntegerBoolean.Adapter())
            .build()
    }
}

@InjectConstructor
class RetrofitProvider(
    private val moshi: Moshi,
    private val okHttpClient: OkHttpClient
): Provider<Retrofit.Builder> {
    override fun get(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(JSONObjectConverterFactory)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
    }
}
