package audio.rabid.vinylscrobbler.data

import com.fixdapp.android.logger.Logger
import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor(private val logger: Logger) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        logger.d("REQUEST", chain.request().method(),
            chain.request().url(),
            chain.request().body()?.contentLength() ?: 0)
        return chain.proceed(chain.request()).also { response ->
            logger.d("RESPONSE", chain.request().method(),
                chain.request().url(),
                response.code(),
                response.body()?.contentLength() ?: 0)
        }
    }
}
