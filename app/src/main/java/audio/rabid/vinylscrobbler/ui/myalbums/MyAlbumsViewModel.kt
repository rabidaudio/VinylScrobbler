package audio.rabid.vinylscrobbler.ui.myalbums

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import audio.rabid.vinylscrobbler.core.ViewModel
import audio.rabid.vinylscrobbler.data.AppDatabase
import audio.rabid.vinylscrobbler.data.models.Album

class MyAlbumsViewModel(
    private val appDatabase: AppDatabase
) : ViewModel() {

    data class State(val albums: List<Album>)

    val state: LiveData<State> = liveData {
        // start with empty list
        emit(State(emptyList()))

        val albums = appDatabase.albumDao().getAll()
        emit(State(albums))
    }
}
