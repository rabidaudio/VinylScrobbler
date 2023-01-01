package audio.rabid.vinylscrobbler.data.lastfm

import audio.rabid.vinylscrobbler.core.adapters.IntegerBoolean
import audio.rabid.vinylscrobbler.core.adapters.Wrapped
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.HttpUrl
import java.util.*

object LastFM {
    @JsonClass(generateAdapter = true)
    @Wrapped("session")
    data class AuthGetMobileSessionResponse(
        @IntegerBoolean
        @Json(name = "subscriber") val subscriber: Boolean,
        @Json(name = "name") val name: String,
        @Json(name = "key") val sessionKey: String
    )

//    @JsonClass(generateAdapter = true)
//    @Wrapped("album")
//    data class Album(
//        @Json(name = "name") val name: String,
//        @Json(name = "artist") val artist: String,
//        @Json(name = "url") val url: HttpUrl,
//        @Json(name = "image") val images: List<Image>,
//        @Json(name = "mbid") val musicBrainzRecordingId: UUID?,
//        @Json(name = "listeners") val listeners: Int?,
//        @Json(name = "playcount") val playCount: Int?
//    ) {
//
//        @JsonClass(generateAdapter = true)
//        data class Image(
//            @Json(name = "#text") val url: HttpUrl,
//            @Json(name = "size") val size: Size
//        ) {
//            enum class Size {
//                @Json(name = "") DEFAULT,
//                @Json(name = "small") SMALL,
//                @Json(name = "medium") MEDIUM,
//                @Json(name = "large") LARGE,
//                @Json(name = "extralarge") EXTRA_LARGE,
//                @Json(name = "mega") MEGA
//            }
//        }
//
//        val largestImage: Image? get() = images.maxBy { it.size }
//    }
}
