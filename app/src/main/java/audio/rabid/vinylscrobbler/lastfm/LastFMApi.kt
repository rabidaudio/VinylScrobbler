package audio.rabid.vinylscrobbler.lastfm

import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import toothpick.InjectConstructor
import java.time.Instant
import java.util.*
import javax.inject.Provider

// https://www.last.fm/api/intro
interface LastFMApi {

    companion object {
        const val BASE_URL = "https://ws.audioscrobbler.com/2.0/"
    }

    @InjectConstructor
    class Factory(
        private val retrofitBuilder: Retrofit.Builder,
        private val okHttpClient: OkHttpClient,
        private val lastFMAuthenticationInterceptor: LastFMAuthenticationInterceptor
    ) : Provider<LastFMApi> {
        override fun get(): LastFMApi {
            return retrofitBuilder
                .baseUrl(BASE_URL)
                .client(okHttpClient.newBuilder()
                    .addInterceptor(lastFMAuthenticationInterceptor)
                    .build())
                .build()
                .create()
        }
    }

    @POST(BASE_URL)
    @FormUrlEncoded
    @LastFMMethod(method = "auth.getMobileSession", authenticated = false, signed = true)
    suspend fun authenticate(
        @Field("username") username: String,
        @Field("password") password: String
    ): AuthGetMobileSessionResponse

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
        @Field("duration") durationSeconds: Int? = null
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
        @Field("duration") durationSeconds: Int? = null
    ): JSONObject
}
