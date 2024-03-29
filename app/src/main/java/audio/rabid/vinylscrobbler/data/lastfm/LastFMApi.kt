package audio.rabid.vinylscrobbler.data.lastfm

import org.json.JSONObject
import retrofit2.http.*
import java.time.Instant
import java.util.*

// https://www.last.fm/api/intro
interface LastFMApi {
    companion object {
        const val BASE_URL = "https://ws.audioscrobbler.com/2.0/"
    }

    @POST(BASE_URL)
    @FormUrlEncoded
    @LastFMMethod(method = "auth.getMobileSession", authenticated = false, signed = true)
    suspend fun authenticate(
        @Field("username") username: String,
        @Field("password") password: String
    ): LastFM.AuthGetMobileSessionResponse

    // https://www.last.fm/api/show/track.scrobble
    // https://www.last.fm/api/scrobbling
    @POST(BASE_URL)
    @FormUrlEncoded
    @LastFMMethod(method = "track.scrobble", authenticated = true, signed = true)
    suspend fun scrobble(
        @Field("artist") artistName: String,
        @Field("track") trackName: String,
        @Field("timestamp") timestamp: Instant,
        @Field("album") albumName: String? = null,
        @Field("trackNumber") trackNumber: Int? = null,
        @Field("mbid") musicBrainzTrackId: UUID? = null,
        @Field("albumArtist") albumArtistName: String? = null,
        @Field("duration") durationSeconds: Long? = null
    ): JSONObject

    // https://www.last.fm/api/show/track.updateNowPlaying
    @POST(BASE_URL)
    @FormUrlEncoded
    @LastFMMethod(method = "track.updateNowPlaying", authenticated = true, signed = true)
    suspend fun updateNowPlaying(
        @Field("artist") artistName: String,
        @Field("track") trackName: String,
        @Field("album") albumName: String? = null,
        @Field("trackNumber") trackNumber: Int? = null,
        @Field("mbid") musicBrainzTrackId: UUID? = null,
        @Field("albumArtist") albumArtistName: String? = null,
        @Field("duration") durationSeconds: Long? = null
    ): JSONObject

    // https://www.last.fm/api/show/album.getInfo
//    @GET(BASE_URL)
//    @LastFMMethod(method = "album.getInfo")
//    suspend fun getAlbumInfo(@Query("mbid") musicBrainzReleaseId: UUID,
//                             @Query("username") username: String? = null): LastFM.Album
}
