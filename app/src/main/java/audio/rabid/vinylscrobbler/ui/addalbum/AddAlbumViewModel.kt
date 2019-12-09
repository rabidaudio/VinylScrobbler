package audio.rabid.vinylscrobbler.ui.addalbum

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import audio.rabid.vinylscrobbler.core.ViewModel
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
        emit(
            State(
                query = "", results =
                listOf(
                    SearchResult(
                        id = UUID.randomUUID(),
                        albumName = "Low",
                        artistName = "Grayling",
                        coverUrl = HttpUrl.get("https://lastfm.freetls.fastly.net/i/u/300x300/dacf29fc6d4bd056ae1c89871a1f3c61.webp"),
                        releaseDate = LocalDate.now()
                    ), SearchResult(
                        id = UUID.randomUUID(),
                        albumName = "Everyone Everywhere",
                        artistName = "Everyone Everywhere",
                        coverUrl = HttpUrl.get("https://lastfm.freetls.fastly.net/i/u/500x500/669d8dc272c94fcb9843ce20ea0734a4.webp"),
                        releaseDate = LocalDate.now()
                    ), SearchResult(
                        id = UUID.randomUUID(),
                        albumName = "the yunahon mixtape",
                        artistName = "oso oso",
                        coverUrl = HttpUrl.get("https://lastfm.freetls.fastly.net/i/u/500x500/1a8d456828eb09478daadcc7933238cf.webp"),
                        releaseDate = LocalDate.now()
                    ), SearchResult(
                        id = UUID.randomUUID(),
                        albumName = "Summer Death",
                        artistName = "Marietta",
//                        coverUrl = HttpUrl.get("https://lastfm.freetls.fastly.net/i/u/500x500/794d8e4676f547b881d795ebeb5b3abe.webp"),
                        coverUrl = null,
                        releaseDate = LocalDate.now()
                    )
                )
            )
        )
    }
}
