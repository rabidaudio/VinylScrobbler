package audio.rabid.vinylscrobbler.data.lastfm

import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import retrofit2.Invocation

class LastFMAuthenticationInterceptor(
    private val apiKey: String,
    private val apiSecret: String,
    private val userAgent: String
) : Interceptor, LastFMSessionManager {

    private var sessionKey: String? = null

    override fun setSession(session: LastFM.AuthGetMobileSessionResponse) {
        sessionKey = session.sessionKey
    }

    override fun clearSession() {
        sessionKey = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val config = chain.request().tag(Invocation::class.java)
            ?.method()?.getDeclaredAnnotation(LastFMMethod::class.java)
            ?: return chain.proceed(originalRequest)
        val additionalParameters = mutableMapOf(
            "api_key" to apiKey,
            "method" to config.method
        )
        // https://www.last.fm/api/authspec
        // https://www.last.fm/api/mobileauth
        if (config.authenticated) {
            val sk = sessionKey ?: throw LastFMUnauthenticatedException(
                config.method
            )
            additionalParameters["sk"] = sk
        }
        if (config.signed) {
            val allParameters = originalRequest.getParameters() + additionalParameters
            additionalParameters["api_sig"] = getSignature(allParameters)
        }
        val newRequest = originalRequest.newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("User-Agent", userAgent)
            .build()
            .addParameters(additionalParameters)
            .addQueryParameters(mapOf("format" to "json"))
        return chain.proceed(newRequest)
    }

    private fun getSignature(parameters: Map<String, String>): String {
        val buffer = Buffer()
        for ((key, value) in parameters.entries.sortedBy { it.key }) {
            buffer.writeUtf8(key)
            buffer.writeUtf8(value)
        }
        buffer.writeUtf8(apiSecret)
        return buffer.md5().hex()
    }
}

fun Request.getParameters(): Map<String, String> {
    return if (isFormPost()) {
        val body = (body() as FormBody)
        mutableMapOf<String, String>().apply {
            for (i in 0 until body.size()) {
                put(body.name(i), body.value(i))
            }
        }
    } else {
        val url = url()
        mutableMapOf<String, String>().apply {
            for (i in 0 until url.querySize()) {
                put(url.queryParameterName(i), url.queryParameterValue(i))
            }
        }
    }
}

fun Request.addParameters(parameters: Map<String, String>): Request =
    if (isFormPost()) addBodyParameters(parameters) else addQueryParameters(parameters)

private fun Request.isFormPost(): Boolean = method() == "POST" && body() is FormBody

private fun Request.addQueryParameters(parameters: Map<String, String>): Request {
    return newBuilder().apply {
        url(url().newBuilder().apply {
            for ((key, value) in parameters) {
                addQueryParameter(key, value)
            }
        }.build())
    }.build()
}

private fun Request.addBodyParameters(parameters: Map<String, String>): Request {
    return newBuilder().apply {
        post((body() as FormBody).addParameters(parameters))
    }.build()
}

private fun FormBody.addParameters(parameters: Map<String, String>): FormBody {
    val builder = FormBody.Builder()
    for (i in 0 until size()) {
        builder.add(name(i), value(i))
    }
    for ((key, value) in parameters) {
        builder.add(key, value)
    }
    return builder.build()
}
