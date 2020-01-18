package audio.rabid.vinylscrobbler.ui.addalbum

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import audio.rabid.vinylscrobbler.core.ViewModel
import audio.rabid.vinylscrobbler.data.TestData
import okhttp3.HttpUrl
import java.time.LocalDate
import java.util.*

class AddAlbumViewModel : ViewModel() {

    data class SearchResult(
        val id: UUID,
        val albumName: String,
        val artistName: String,
        val coverUrl: HttpUrl?,
        val releaseDate: LocalDate
//        val playCount: Int?,
//        val format: String
    )

    data class State(val query: String, val results: List<SearchResult>)

    fun search(query: String) {

    }

    val state: LiveData<State> = liveData {
        emit(State(query = "", results = TestData.searchResults))
    }
}
