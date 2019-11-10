package audio.rabid.vinylscrobbler

import android.content.Context
import audio.rabid.vinylscrobbler.core.adapters.*
import audio.rabid.vinylscrobbler.data.AppDatabase
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
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

//    bind<Context>().toInstance(applicationContext)

    bind<OkHttpClient>().toInstance {
        OkHttpClient.Builder()
//            .addInterceptor(OkReplayInterceptor())
            .build()
    }

    bind<Moshi>().toProvider(MoshiProvider::class).providesSingleton()
    bind<Retrofit.Builder>().toProvider(RetrofitProvider::class).providesSingleton()
    bind<AppDatabase>().toProvider(AppDatabaseProvider::class).providesSingleton()
}

@InjectConstructor
private class AppDatabaseProvider(val applicationContext: Context) : Provider<AppDatabase> {
    override fun get(): AppDatabase = AppDatabase.get(applicationContext)
}

@InjectConstructor
private class MoshiProvider : Provider<Moshi> {
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
private class RetrofitProvider(
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
