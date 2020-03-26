package audio.rabid.vinylscrobbler.core.adapters

import androidx.room.TypeConverter
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import okhttp3.HttpUrl
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.*

class HttpUrlAdapter {
    @TypeConverter
    @ToJson
    fun toJson(url: HttpUrl?): String? = url?.toString()

    @TypeConverter
    @FromJson
    fun fromJson(url: String?): HttpUrl? = url?.nullIfBlank()?.let { HttpUrl.parse(it) }
}


class UUIDAdapter {
    @TypeConverter
    @ToJson
    fun toJson(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    @FromJson
    fun fromJson(uuid: String?): UUID? = uuid?.nullIfBlank()?.let { UUID.fromString(it) }
}

class InstantAdapter {
    @TypeConverter
    @ToJson
    fun toJson(instant: Instant?): String? = instant?.toString()

    @TypeConverter
    @FromJson
    fun fromJson(dateString: String?): Instant? = dateString?.let { Instant.parse(it) }
}

class LocalDateAdapter {
    @TypeConverter
    @ToJson
    fun toJson(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    @FromJson
    fun fromJson(dateString: String?): LocalDate? = dateString?.let { s ->
        try {
            LocalDate.parse(s)
        } catch (e: DateTimeParseException) {
            null
        }
    }
}

class DurationAdapter {
    @TypeConverter
    fun toJson(duration: Duration): String = duration.toString()

    @TypeConverter
    fun fromJson(durationString: String): Duration = Duration.parse(durationString)
}

@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IntegerBoolean {
    class Adapter {
        @ToJson
        fun toJson(@IntegerBoolean bool: Boolean): Int = if (bool) 1 else 0

        @IntegerBoolean
        @FromJson
        fun fromJson(int: Int): Boolean = int != 0
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun String.nullIfBlank(): String? = takeUnless { it.isBlank() }
