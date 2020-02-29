package audio.rabid.vinylscrobbler.ui.addalbum

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import audio.rabid.vinylscrobbler.core.ViewModel
import audio.rabid.vinylscrobbler.data.musicbrainz.MusicBrainz
import audio.rabid.vinylscrobbler.data.musicbrainz.MusicBrainzApi
import com.fixdapp.android.logger.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import okhttp3.HttpUrl
import java.time.LocalDate
import java.util.*

class AddAlbumViewModel(
    private val musicBrainzApi: MusicBrainzApi,
    private val logger: Logger
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 25
    }

    data class SearchResult(
        val id: UUID,
        val albumName: String,
        val artistName: String,
        val coverUrl: HttpUrl?,
        val releaseDate: LocalDate?
    )

    data class State(val query: String, val page: Int, val results: List<SearchResult>)

    private val queryChannel = Channel<Pair<String, Int>>(Channel.BUFFERED)

    fun search(query: String) {
        queryChannel.offer(query to 0)
    }

    fun loadNextPage() {
        state.value?.let { state -> queryChannel.offer(state.query to state.page + 1) }
    }

    // mock
//    val state: LiveData<State> = liveData {
//        emit(State(query = "", results = TestData.searchResults))
//    }

    @UseExperimental(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val state: LiveData<State> = liveData {
        queryChannel.consumeAsFlow()
            .debounce(500)
            .mapLatest { (query, page) -> query(query, page) }
            .collect(this::emit)
    }

    private suspend fun query(query: String, page: Int): State {
        if (query.isBlank()) {
            // MusicBrainz returns a 400 for an empty query
            return State(query = query, page = page, results = emptyList())
        }
        val result =
            musicBrainzApi.searchReleases(query, limit = PAGE_SIZE, offset = page * PAGE_SIZE)
        logger.d("api results", result)
        val initialResults = state.value?.results?.takeUnless { page == 0 } ?: emptyList()
        return State(query = query, page = page, results = initialResults + result.toSearchResults())
    }

    private fun MusicBrainz.ReleaseQueryResult.toSearchResults(): List<SearchResult> =
        releases.map { release -> release.toSearchResult() }

    private fun MusicBrainz.Release.toSearchResult(): SearchResult {
        return SearchResult(
            id = id,
            albumName = title,
            artistName = artistCredits.first().name, // TODO deal with multi-artist releases
            releaseDate = date,
            coverUrl = MusicBrainzApi.coverImageUrl(id)
        )
    }
}
