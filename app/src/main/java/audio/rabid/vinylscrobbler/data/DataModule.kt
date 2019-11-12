package audio.rabid.vinylscrobbler.data

import android.content.Context
import audio.rabid.vinylscrobbler.core.adapters.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
object DataModule {

    @Provides
    @ElementsIntoSet
    @Named("MoshiAdapters")
    @JvmStatic
    fun commonAdapters(): Set<Any> {
        return setOf(
            WrappedAdapterFactory,
            HttpUrlAdapter(),
            UUIDAdapter(),
            InstantAdapter()
        )
    }

    // TODO: IntegerBoolean.Adapter() is LAstfmspecific

    @Provides
    @Singleton
    @JvmStatic
    fun moshi(@Named("MoshiAdapters") adapters: Set<Any>): Moshi {
        return Moshi.Builder().apply {
            for (adapter in adapters) {
                if (adapter is JsonAdapter.Factory) {
                    add(adapter)
                } else {
                    add(adapter)
                }
            }
        }.build()
    }

    @Provides
    @Singleton
    @JvmStatic
    fun database(applicationContext: Context): AppDatabase {
        return AppDatabase.get(applicationContext)
    }

    @Provides
    @ElementsIntoSet
    @JvmStatic
    fun defaultHttpInterceptors(): Set<Interceptor> {
        return setOf(
//            OkReplayInterceptor() // TODO testing only
        )
    }

    @Provides
    @JvmStatic
    fun okHttpClient(defaultInterceptors: Set<Interceptor>): OkHttpClient {
        return OkHttpClient.Builder().apply {
            for (interceptor in defaultInterceptors) {
                addInterceptor(interceptor)
            }
        }.build()
    }

    @Provides
    @JvmStatic
    fun retrofitBuilder(moshi: Moshi, client: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(JSONObjectConverterFactory)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
    }
}
