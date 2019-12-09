package audio.rabid.vinylscrobbler.data.musicbrainz

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.util.*

object MusicBrainz {

    @JsonClass(generateAdapter = true)
    data class ReleaseQueryResult(
        @Json(name = "created") val created: Instant,
        @Json(name = "count") val count: Int,
        @Json(name = "offset") val offset: Int,
        @Json(name = "releases") val releases: List<Release>
    )

    @JsonClass(generateAdapter = true)
    data class Artist(
        @Json(name = "id") val id: UUID,
        @Json(name = "name") val name: String,
        @Json(name = "sort-name") val sortName: String,
        @Json(name = "disambiguation") val disambiguation: String
    )

    @JsonClass(generateAdapter = true)
    data class Track(
        @Json(name = "id") val id: UUID,
        @Json(name = "number") val number: Int,
        @Json(name = "length") val durationMillis: Long,
        @Json(name = "title") val title: String
    ){
        val duration: Duration get() = Duration.ofMillis(durationMillis)
    }

    @JsonClass(generateAdapter = true)
    data class Release(
        @Json(name = "id") val id: UUID,
        @Json(name = "title") val title: String,
        @Json(name = "artist-credit") val artistCredits: List<ArtistCredit>,
        @Json(name = "date") val date: LocalDate,
        @Json(name = "country") val country: String,
        @Json(name = "track-count") val tackCount: Int,
        @Json(name = "media") val media: List<Media>
    )  {

        @JsonClass(generateAdapter = true)
        data class ArtistCredit(
            @Json(name = "name") val name: String,
            @Json(name = "artist") val artist: Artist
        )

        @JsonClass(generateAdapter = true)
        data class Media(
            @Json(name = "format") val format: String,
            @Json(name = "disk-count") val diskCount: Int?,
            @Json(name = "track-offset") val trackOffset: Int?,
            @Json(name = "position") val position: Int?,
            @Json(name = "track-count") val trackCount: Int,
            @Json(name = "tracks") val tracks: List<Track>?
        )
    }
}
