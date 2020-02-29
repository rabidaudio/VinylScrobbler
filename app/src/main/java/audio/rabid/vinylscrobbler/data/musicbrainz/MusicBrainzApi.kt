package audio.rabid.vinylscrobbler.data.musicbrainz

import audio.rabid.vinylscrobbler.BuildConfig
import audio.rabid.vinylscrobbler.data.UserAgentInterceptor
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

// https://musicbrainz.org/doc/Development/XML_Web_Service/Version_2
// https://musicbrainz.org/doc/MusicBrainz_Database/Schema
interface MusicBrainzApi {
    companion object {

        private const val BASE_URL = "https://musicbrainz.org/ws/2/"

        private const val USER_AGENT =
            "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME} ${BuildConfig.CONTACT_EMAIL}"

        fun create(
            retrofitBuilder: Retrofit.Builder,
            okHttpClient: OkHttpClient
        ): MusicBrainzApi {
            return retrofitBuilder
                .baseUrl(BASE_URL)
                .client(
                    okHttpClient.newBuilder()
                        .addInterceptor(UserAgentInterceptor(USER_AGENT))
                        .build()
                )
                .build()
                .create()
        }

        fun coverImageUrl(releaseId: UUID): HttpUrl? =
            HttpUrl.parse("https://coverartarchive.org/release/${releaseId}/front-500.jpg")
    }

    @GET("release?fmt=json")
    suspend fun searchReleases(
        @Query("query") query: String,
        @Query("limit") limit: Int = 25,
        @Query("offset") offset: Int = 0
    ): MusicBrainz.ReleaseQueryResult

    @GET("release/{releaseId}?inc=media+recordings&fmt=json")
    suspend fun lookupRelease(@Path("releaseId") releaseId: UUID): MusicBrainz.Release
}
