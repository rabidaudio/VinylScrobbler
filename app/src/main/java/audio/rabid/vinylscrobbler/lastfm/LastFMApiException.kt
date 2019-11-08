package audio.rabid.vinylscrobbler.lastfm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

class LastFMApiException(val code: Int, message: String) : Exception(message) {
    @JsonClass(generateAdapter = true)
    data class Response(
        @Json(name = "error") val code: Int,
        @Json(name = "message") val message: String
    )
}
