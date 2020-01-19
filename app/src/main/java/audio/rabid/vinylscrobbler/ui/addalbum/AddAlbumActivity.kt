package audio.rabid.vinylscrobbler.ui.addalbum

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import audio.rabid.kaddi.Kaddi
import audio.rabid.kaddi.inject
import audio.rabid.vinylscrobbler.R
import audio.rabid.vinylscrobbler.core.ui.ViewBindingAdapter
import audio.rabid.vinylscrobbler.core.ui.setOnEndlessScrollLoadMoreCallback
import audio.rabid.vinylscrobbler.core.ui.viewBinding
import audio.rabid.vinylscrobbler.databinding.ActivityAddAlbumBinding
import audio.rabid.vinylscrobbler.databinding.ListAlbumViewBinding
import audio.rabid.vinylscrobbler.ui.addalbum.AddAlbumViewModel.SearchResult
import audio.rabid.vinylscrobbler.ui.coverImageLoader

class AddAlbumActivity : AppCompatActivity() {

    private val viewModel by inject<AddAlbumViewModel>()
    private val views by viewBinding(ActivityAddAlbumBinding::inflate)
    private val searchResultsAdapter = SearchResultsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Kaddi.getScope(application)
            .createChildScope(this, AddAlbumModule)
            .inject(this)

        with(views.ResultsList) {
            adapter = searchResultsAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
                // addItemDecoration(DividerItemDecoration(context, orientation))
            }
            setOnEndlessScrollLoadMoreCallback(rowThreshold = 3) {
                // TODO load next page of results
            }
        }

        views.SearchToolbar.setQueryFromIntent(intent)
        views.SearchToolbar.setOnSearchListener { query ->
            viewModel.search(query.toString())
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        views.SearchToolbar.setQueryFromIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.observe(this) { state ->
            searchResultsAdapter.setItems(state.results)
        }
    }

    override fun onDestroy() {
        Kaddi.closeScope(this)
        super.onDestroy()
    }

    private inner class SearchResultsAdapter
        : ViewBindingAdapter<SearchResult, ListAlbumViewBinding>(ListAlbumViewBinding::inflate) {

        override fun bind(item: SearchResult, viewBinding: ListAlbumViewBinding) {
            viewBinding.setResult(item)
        }

        override fun isSameItem(a: SearchResult, b: SearchResult): Boolean = a.id == b.id

        override fun onItemClick(item: SearchResult, viewBinding: ListAlbumViewBinding) {
            // TODO
        }
    }

    fun ListAlbumViewBinding.setResult(searchResult: SearchResult) {
        searchResult.coverUrl.coverImageLoader().placeholder(R.drawable.ic_album).into(Cover)
        Album.text = searchResult.artistName
        Artist.text = searchResult.albumName
        ReleaseYear.text = searchResult.releaseDate.year.toString()
    }
}
