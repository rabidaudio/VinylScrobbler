package audio.rabid.vinylscrobbler.ui.addalbum.search

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
import audio.rabid.vinylscrobbler.databinding.ActivitySearchReleaseGroupsBinding
import audio.rabid.vinylscrobbler.databinding.ListAlbumViewBinding
import audio.rabid.vinylscrobbler.ui.addalbum.search.SearchReleaseGroupsViewModel.ReleaseGroupResult
import audio.rabid.vinylscrobbler.ui.addalbum.add.AddReleaseActivity
import audio.rabid.vinylscrobbler.ui.coverImageLoader

class SearchReleaseGroupsActivity : AppCompatActivity() {

    private val viewModel by inject<SearchReleaseGroupsViewModel>()
    private val views by viewBinding(ActivitySearchReleaseGroupsBinding::inflate)
    private val searchResultsAdapter = SearchResultsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Kaddi.getScope(application)
            .createChildScope(this,
                SearchReleaseGroupsModule
            )
            .inject(this)

        with(views.ResultsList) {
            adapter = searchResultsAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
                // addItemDecoration(DividerItemDecoration(context, orientation))
            }
            setOnEndlessScrollLoadMoreCallback(rowThreshold = 3) {
                viewModel.loadNextPage()
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

    private fun goToAddRelease(releaseGroup: ReleaseGroupResult) {
        AddReleaseActivity.getLauncher(
            AddReleaseActivity.Arguments(
            releaseGroupId = releaseGroup.releaseGroupId,
            coverUrl = releaseGroup.coverUrl?.uri(),
            artistName = releaseGroup.artistName,
            albumName = releaseGroup.albumName
        )).startActivity(this)
    }

    private inner class SearchResultsAdapter : ViewBindingAdapter<ReleaseGroupResult,
            ListAlbumViewBinding>(ListAlbumViewBinding::inflate) {

        override fun bind(item: ReleaseGroupResult, viewBinding: ListAlbumViewBinding) {
            viewBinding.setResult(item)
        }

        override fun isSameItem(a: ReleaseGroupResult, b: ReleaseGroupResult): Boolean =
            a.releaseGroupId == b.releaseGroupId

        override fun onItemClick(item: ReleaseGroupResult, viewBinding: ListAlbumViewBinding) {
            goToAddRelease(item)
        }
    }

    fun ListAlbumViewBinding.setResult(searchResult: ReleaseGroupResult) {
        searchResult.coverUrl.coverImageLoader().placeholder(R.drawable.ic_album).into(Cover)
        Album.text = searchResult.albumName
        Artist.text = searchResult.artistName
        ReleaseDetails.text = searchResult.artistDisambiguation ?: ""
    }
}
