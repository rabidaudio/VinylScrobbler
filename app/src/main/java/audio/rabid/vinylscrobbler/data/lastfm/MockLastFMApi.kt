package audio.rabid.vinylscrobbler.data.lastfm

import com.fixdapp.android.logger.Logger
import org.json.JSONObject
import java.time.Instant
import java.util.*

class MockLastFMApi(private val logger: Logger): LastFMApi {
    override suspend fun authenticate(
        username: String,
        password: String
    ): LastFM.AuthGetMobileSessionResponse {
        logger.d("authenticated user", username)
        return LastFM.AuthGetMobileSessionResponse(true, username, "FAKE_KEY")
    }

    override suspend fun scrobble(
        artistName: String,
        trackName: String,
        timestamp: Instant,
        albumName: String?,
        trackNumber: Int?,
        musicBrainzTrackId: UUID?,
        albumArtistName: String?,
        durationSeconds: Long?
    ): JSONObject {
        logger.d("scrobble", trackName, artistName, albumName)
        return JSONObject()
    }

    override suspend fun updateNowPlaying(
        artistName: String,
        trackName: String,
        albumName: String?,
        trackNumber: Int?,
        musicBrainzTrackId: UUID?,
        albumArtistName: String?,
        durationSeconds: Long?
    ): JSONObject {
        logger.d("updateNowPlaying", trackName, artistName, albumArtistName)
        return JSONObject()
    }
}
