package audio.rabid.vinylscrobbler.data

import audio.rabid.vinylscrobbler.data.db.models.Album
import audio.rabid.vinylscrobbler.ui.addalbum.AddAlbumViewModel
import okhttp3.HttpUrl
import java.time.LocalDate
import java.util.*

// STOPSHIP
object TestData {

    val data = listOf(
        AddAlbumViewModel.SearchResult(
            id = UUID.randomUUID(),
            albumName = "Low",
            artistName = "Grayling",
            coverUrl = HttpUrl.get("https://lastfm.freetls.fastly.net/i/u/300x300/dacf29fc6d4bd056ae1c89871a1f3c61.webp"),
            releaseDate = LocalDate.now()
        ), AddAlbumViewModel.SearchResult(
            id = UUID.randomUUID(),
            albumName = "Everyone Everywhere",
            artistName = "Everyone Everywhere",
            coverUrl = HttpUrl.get("https://lastfm.freetls.fastly.net/i/u/500x500/669d8dc272c94fcb9843ce20ea0734a4.webp"),
            releaseDate = LocalDate.now()
        ), AddAlbumViewModel.SearchResult(
            id = UUID.randomUUID(),
            albumName = "the yunahon mixtape",
            artistName = "oso oso",
            coverUrl = HttpUrl.get("https://lastfm.freetls.fastly.net/i/u/500x500/1a8d456828eb09478daadcc7933238cf.webp"),
            releaseDate = LocalDate.now()
        ), AddAlbumViewModel.SearchResult(
            id = UUID.randomUUID(),
            albumName = "Summer Death",
            artistName = "Marietta",
//                        coverUrl = HttpUrl.get("https://lastfm.freetls.fastly.net/i/u/500x500/794d8e4676f547b881d795ebeb5b3abe.webp"),
            coverUrl = null,
            releaseDate = LocalDate.now()
        )
    )

    val searchResults = listOf(data, data, data).flatten()

    val albums get() = listOf(searchResults, searchResults, searchResults).flatten().map {
        Album(
            name = it.albumName,
            artistName = it.artistName,
            musicBrainzReleaseId = it.id,
            coverUrl = it.coverUrl
        )
    }
}
