package audio.rabid.vinylscrobbler.lastfm

import audio.rabid.vinylscrobbler.IntegerBoolean
import audio.rabid.vinylscrobbler.Wrapped
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Wrapped("session")
data class AuthGetMobileSessionResponse(
    @IntegerBoolean
    @Json(name = "subscriber") val subscriber: Boolean,
    @Json(name = "name") val name: String,
    @Json(name = "key") val sessionKey: String
)

//@JsonClass(generateAdapter = true)
//data class Album(
//    @Json(name = "name") val name: String,
//    @Json(name = "artist") val artist: String,
//    @Json(name = "url") val url: HttpUrl,
//    @Json(name = "image") val images: List<Image>,
//    @Json(name = "mbid") val musicBrainzRecordingId: UUID?
//) {
//
//    data class Image(
//        @Json(name = "#text") val url: HttpUrl,
//        @Json(name = "size") val size: Size
//    ) {
//        enum class Size { small, medium, large, extralarge }
//    }
//}
