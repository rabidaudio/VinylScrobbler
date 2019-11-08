package audio.rabid.vinylscrobbler

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import okhttp3.HttpUrl
import java.time.Instant
import java.util.*

object HttpUrlAdapter {
    @ToJson
    fun toJson(url: HttpUrl): String = url.toString()

    @FromJson
    fun fromJson(url: String): HttpUrl? = url.nullIfBlank()?.let { HttpUrl.parse(it) }
}

object UUIDAdapter {
    @ToJson
    fun toJson(uuid: UUID): String = uuid.toString()

    @FromJson
    fun fromJson(uuid: String): UUID? = uuid.nullIfBlank()?.let { UUID.fromString(it) }
}

object InstantAdapter {
    @ToJson
    fun toJson(instant: Instant): String = instant.toString()

    @FromJson
    fun fromJson(dateString: String): Instant = Instant.parse(dateString)
}

@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IntegerBoolean {
    object Adapter {
        @ToJson
        fun toJson(@IntegerBoolean bool: Boolean): Int = if (bool) 1 else 0

        @IntegerBoolean
        @FromJson
        fun fromJson(int: Int): Boolean = int != 0
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun String.nullIfBlank(): String? = takeUnless { it.isBlank() }
