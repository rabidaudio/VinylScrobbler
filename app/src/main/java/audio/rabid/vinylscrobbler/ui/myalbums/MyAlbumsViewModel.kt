package audio.rabid.vinylscrobbler.ui.myalbums

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import audio.rabid.vinylscrobbler.core.ViewModel
import audio.rabid.vinylscrobbler.data.db.AppDatabase
import audio.rabid.vinylscrobbler.data.db.models.Album
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.startWith

@UseExperimental(ExperimentalCoroutinesApi::class)
class MyAlbumsViewModel(appDatabase: AppDatabase) : ViewModel() {

    data class State(val albums: List<Album>)

    val  state: LiveData<State> =  liveData {
//        emit(State(TestData.albums))
        emit(State(emptyList()))
    }

//    val state: LiveData<State> = appDatabase.albumDao().getAll()
//        .map { State(it) }
//        .onStart { emit(State(emptyList())) }
//        .asLiveData()
}
