package audio.rabid.vinylscrobbler.ui.myalbums

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import audio.rabid.vinylscrobbler.core.ViewModel
import audio.rabid.vinylscrobbler.data.db.AppDatabase
import audio.rabid.vinylscrobbler.data.db.models.Album
import audio.rabid.vinylscrobbler.data.db.models.Track
import audio.rabid.vinylscrobbler.data.musicbrainz.MusicBrainzApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import okhttp3.HttpUrl
import java.time.Duration
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class MyAlbumsViewModel(private val appDatabase: AppDatabase) : ViewModel() {

    data class State(val searchTerm: String? = null, val albums: List<Album>)

    private val searchTerms = Channel<String?>()

//    init {
//        launch {
//            appDatabase.albumDao().insert(
//                Album(
//                    name = "When You Walk a Long Distance You Are Tired",
//                    artistName = "Mothers",
//                    musicBrainzReleaseId = UUID.fromString("5a61f846-e7d1-4fa8-a713-8085e4823972"),
//                    coverUrl = HttpUrl.parse("https://coverartarchive.org/release-group/1aca6863-4dda-4a6e-8818-e1bc8c756373/front-500.jpg")
//                )
//            )
//            appDatabase.trackDao().insert(listOf(
//                Track(trackNumber = "A1", position = 1, title = "Too Small for Eyes", duration = Duration.ofSeconds(347), artistName = "Mothers", albumName = "When You Walk a Long Distance You Are Tired", musicBrainzTrackId = UUID.fromString("f305505a-9738-42cf-8446-6d27987ca843"), albumId = 1),
//                Track(trackNumber = "A2", position = 2, title = "It Hurts Until It Doesn’t", duration = Duration.ofSeconds(334), artistName = "Mothers", albumName = "When You Walk a Long Distance You Are Tired", musicBrainzTrackId = UUID.fromString("37f8b376-66fa-4ede-96d4-9afdf0c62d97"), albumId = 1),
//                Track(trackNumber = "A3", position = 3, title = "Copper Mines", duration = Duration.ofSeconds(237), artistName = "Mothers", albumName = "When You Walk a Long Distance You Are Tired", musicBrainzTrackId = UUID.fromString("d74dc92d-4b5d-485c-b435-e913b99639c1"), albumId = 1),
//                Track(trackNumber = "A4", position = 4, title = "Nesting Behavior", duration = Duration.ofSeconds(377), artistName = "Mothers", albumName = "When You Walk a Long Distance You Are Tired", musicBrainzTrackId = UUID.fromString("da35560b-5590-4569-893d-851f7cd36359"), albumId = 1),
//                Track(trackNumber = "B1", position = 5, title = "Lockjaw", duration = Duration.ofSeconds(319), artistName = "Mothers", albumName = "When You Walk a Long Distance You Are Tired", musicBrainzTrackId = UUID.fromString("cb5cce45-cbed-4ebe-b041-a884bb431c0a"), albumId = 1),
//                Track(trackNumber = "B2", position = 6, title = "Blood‐letting", duration = Duration.ofSeconds(337), artistName = "Mothers", albumName = "When You Walk a Long Distance You Are Tired", musicBrainzTrackId = UUID.fromString("dc3765c3-6a87-4361-86e6-8226a44a7f14"), albumId = 1),
//                Track(trackNumber = "B3", position = 7, title = "Burden of Proof", duration = Duration.ofSeconds(183), artistName = "Mothers", albumName = "When You Walk a Long Distance You Are Tired", musicBrainzTrackId = UUID.fromString("a2124928-21c7-4c51-9171-a035da37c0a1"), albumId = 1),
//                Track(trackNumber = "B4", position = 8, title = "Hold Your Own Hand", duration = Duration.ofSeconds(414), artistName = "Mothers", albumName = "When You Walk a Long Distance You Are Tired", musicBrainzTrackId = UUID.fromString("6ad027b6-6432-4bac-868d-c5eb34896985"), albumId = 1)
//            ))
//        }
//    }

    fun setSearchTerm(search: String) {
        launch { searchTerms.send(search.trim().ifBlank { null }) }
    }

    // unfortunately this is cold, so the whole benefit of a view-model is lost. It's also easy
    // to get into operator hell
    val state: LiveData<State> = searchTerms.consumeAsFlow()
        .debounce(300L)
        .onStart { emit(null) }
        .flatMapLatest { searchTerm -> getAlbums(searchTerm).toStateFlow(searchTerm) }
        .onStart { emit(State(null, emptyList())) }
        .asLiveData() // todo should we use the view-model's coroutineContext?

    private fun getAlbums(searchTerm: String?): Flow<List<Album>> {
        if (searchTerm == null) return appDatabase.albumDao().getAll()
        return appDatabase.albumDao().search(searchTerm)
    }

    private fun Flow<List<Album>>.toStateFlow(searchTerm: String?): Flow<State> {
        return map { albums -> State(searchTerm, albums) }
    }
}
