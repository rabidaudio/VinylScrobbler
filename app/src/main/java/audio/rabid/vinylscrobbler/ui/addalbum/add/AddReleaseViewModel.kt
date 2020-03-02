package audio.rabid.vinylscrobbler.ui.addalbum.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import audio.rabid.vinylscrobbler.core.ViewModel
import audio.rabid.vinylscrobbler.data.musicbrainz.MusicBrainz
import audio.rabid.vinylscrobbler.data.musicbrainz.MusicBrainzApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.HttpUrl
import java.time.LocalDate
import java.util.*

class AddReleaseViewModel(
    private val arguments: AddReleaseActivity.Arguments,
    private val musicBrainzApi: MusicBrainzApi
) : ViewModel() {

    data class State(
        val releaseGroupId: UUID,
        val coverArtUrl: HttpUrl?,
        val artistName: String,
        val albumName: String,
        val releases: List<ReleaseResult>?
    )

    data class ReleaseResult(
        val releaseId: UUID,
        val releaseDate: LocalDate?,
        val label: String?,
        val country: String?,
        val tracks: List<MusicBrainz.Track>,
        val format: String?
    )

    @UseExperimental(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val state: LiveData<State> = liveData {
        // start by emitting the basic info
        val initialState =
            State(
                releaseGroupId = arguments.releaseGroupId,
                coverArtUrl = arguments.coverUrl?.let { HttpUrl.parse(it.toString()) },
                artistName = arguments.artistName,
                albumName = arguments.albumName,
                releases = null
            )
        emit(initialState)
        // load the releases
        // TODO: api error handling
        // TODO: we are assuming that no release group as more than 1 page (25) of releases
        val res = musicBrainzApi.getReleasesForGroup(arguments.releaseGroupId)
        // emit the complete state
        emit(initialState.copy(releases = res.releases.toReleaseResults()))
    }

    fun addRelease(releaseResult: ReleaseResult) {
        // TODO: update database
    }

    private fun List<MusicBrainz.Release>.toReleaseResults(): List<ReleaseResult> {
        return map { release ->
            ReleaseResult(
                releaseId = release.id,
                releaseDate = release.date,
                label = release.labelInfo.firstOrNull()?.label?.name,
                country = release.country,
                tracks = release.media.first().tracks,
                format = release.media.first().format
            )
        }
    }
}
