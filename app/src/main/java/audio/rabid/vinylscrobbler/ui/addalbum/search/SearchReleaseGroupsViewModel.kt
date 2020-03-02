package audio.rabid.vinylscrobbler.ui.addalbum.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import audio.rabid.vinylscrobbler.core.ViewModel
import audio.rabid.vinylscrobbler.data.musicbrainz.MusicBrainz
import audio.rabid.vinylscrobbler.data.musicbrainz.MusicBrainzApi
import audio.rabid.vinylscrobbler.data.musicbrainz.MusicBrainzApi.Companion.MAX_PAGE_SIZE
import com.fixdapp.android.logger.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import okhttp3.HttpUrl
import java.util.*

class SearchReleaseGroupsViewModel(
    private val musicBrainzApi: MusicBrainzApi,
    private val logger: Logger
) : ViewModel() {

    data class ReleaseGroupResult(
        val releaseGroupId: UUID,
        val albumName: String,
        val artistName: String,
        val artistDisambiguation: String?,
        val coverUrl: HttpUrl?,
        val releaseCount: Int
    )

    data class State(val query: String, val page: Int, val results: List<ReleaseGroupResult>)

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
            return State(
                query = query,
                page = page,
                results = emptyList()
            )
        }
        val result = musicBrainzApi.searchReleaseGroups(query,
            limit = MAX_PAGE_SIZE, offset = page * MAX_PAGE_SIZE)
        logger.d("api results", result)
        val initialResults = state.value?.results?.takeUnless { page == 0 } ?: emptyList()
        return State(
            query = query,
            page = page,
            results = initialResults + result.toSearchResults()
        )
    }

    private fun MusicBrainz.ReleaseGroupQueryResult.toSearchResults(): List<ReleaseGroupResult> =
        releaseGroups.map { releaseGroup -> releaseGroup.toSearchResult() }

    private fun MusicBrainz.ReleaseGroup.toSearchResult(): ReleaseGroupResult {
        return ReleaseGroupResult(
            releaseGroupId = id,
            albumName = title,
            // TODO deal with multi-artist releases
            artistName = artistCredits.first().artist.name,
            artistDisambiguation = artistCredits.first().artist.disambiguation,
            coverUrl = MusicBrainzApi.coverImageUrl(id),
            releaseCount = releaseCount
        )
    }
}
