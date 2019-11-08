package audio.rabid.vinylscrobbler

import com.squareup.moshi.Moshi
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

    bind<Moshi>().toProvider(MoshiProvider::class)
    bind<Retrofit>().toProvider(RetrofitProvider::class)
}

class MoshiProvider : Provider<Moshi> {
    override fun get(): Moshi {
        return         Moshi.Builder()
            // Toothpick doesn't support set bindings yet:
            // https://github.com/stephanenicolas/toothpick/issues/368
            .add(WrappedAdapterFactory)
            .add(HttpUrlAdapter)
            .add(UUIDAdapter)
            .add(InstantAdapter)
            .build()
    }
}

@InjectConstructor
class RetrofitProvider(private val moshi: Moshi): Provider<Retrofit> {
    override fun get(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(JSONObjectConverterFactory)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}
