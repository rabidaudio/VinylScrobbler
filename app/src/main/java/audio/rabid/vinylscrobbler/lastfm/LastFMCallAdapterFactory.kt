package audio.rabid.vinylscrobbler.lastfm

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import java.lang.reflect.Type

object LastFMCallAdapterFactory : CallAdapter.Factory() {

    data class LastFMCall<T>(
        val wrapped: Call<T>,
        val config: LastFMMethod
    ) : Call<T> by wrapped

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val lastFMMethod = annotations.filterIsInstance<LastFMMethod>().firstOrNull() ?: return null
        for (callAdapterFactory in retrofit.callAdapterFactories()) {
            if (callAdapterFactory is LastFMCallAdapterFactory) continue
            val adapter = callAdapterFactory.get(returnType, annotations, retrofit) ?: continue
            return LastFMCallAdapter(adapter, lastFMMethod, retrofit)
        }
        return null
    }

    private class LastFMCallAdapter<R, T>(
        private val wrapped: CallAdapter<R, T>,
        private val config: LastFMMethod,
        private val retrofit: Retrofit
    ) : CallAdapter<R, T> {

        private val apiExceptionConverter by lazy {
            retrofit.responseBodyConverter<LastFMApiException.Response>(
                LastFMApiException.Response::class.java, emptyArray())
        }

        override fun adapt(call: Call<R>): T {
            try {
                return wrapped.adapt(LastFMCall(call, config))
            } catch (e: Exception) {
                throw e.convertToApiExceptionIfPossible()
            }
        }

        override fun responseType(): Type {
            return wrapped.responseType()
        }

        private fun Exception.convertToApiExceptionIfPossible(): Exception {
            val responseBody = (this as? HttpException)?.response()?.errorBody() ?: return this
            val errorBody = apiExceptionConverter.convert(responseBody) ?: return this
            return LastFMApiException(errorBody.code, errorBody.message)
        }
    }
}
