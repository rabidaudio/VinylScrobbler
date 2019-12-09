package audio.rabid.vinylscrobbler.ui.addalbum

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import audio.rabid.kaddi.Kaddi
import audio.rabid.kaddi.inject
import audio.rabid.vinylscrobbler.R
import audio.rabid.vinylscrobbler.core.ui.*
import audio.rabid.vinylscrobbler.ui.Theme
import audio.rabid.vinylscrobbler.ui.addalbum.AddAlbumViewModel.SearchResult
import audio.rabid.vinylscrobbler.ui.cardTheme
import audio.rabid.vinylscrobbler.ui.coverImageLoader
import com.arlib.floatingsearchview.FloatingSearchView

class AddAlbumActivity : AppCompatActivity() {

    private val albumView by bindView(::AddAlbumView)

    private val viewModel by inject<AddAlbumViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(albumView)

        Kaddi.getScope(application)
            .createChildScope(this, AddAlbumModule)
            .inject(this)

        albumView.searchView.setOnQueryChangeListener { _, newQuery ->
            viewModel.search(newQuery)
        }
        if (intent.action == Intent.ACTION_SEARCH) {
            albumView.searchView.setSearchText(intent.getStringExtra(SearchManager.QUERY))
        }
        albumView.searchView.setOnHomeActionClickListener {
            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_SEARCH) {
            albumView.searchView.setSearchText(intent.getStringExtra(SearchManager.QUERY))
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.observe(this) { state ->
            albumView.searchResults.setItems(state.results)
        }
    }

    class AddAlbumView(context: Context) : CustomView(context) {

        val searchView = FloatingSearchView(context).apply {
            setSearchHint(getString(R.string.search))
            setLeftActionMode(FloatingSearchView.LEFT_ACTION_MODE_SHOW_HOME)
            setCloseSearchOnKeyboardDismiss(true)
            setBackgroundColor(Theme.Colors.surface.color)
            setQueryTextColor(Theme.Colors.surface.onColor)
            setLeftActionIconColor(Theme.Colors.surface.onColor)
            setHintTextColor(Theme.Colors.surface.onColor)
            applyLayout(
                x = matchParentX().withDefaultMargin(),
                y = toParentTop().withHalfMargin()
            )
        }

        val searchResults = SearchResultsView(context).apply {
            applyLayout(
                x = matchParentX(),
                y = below(searchView).toParentBottom().withHalfMargin()
            )
        }
    }

    class SearchResultsView(context: Context) : BindingRecyclerView<SearchResult, SearchResultView>(context) {
        init {
            layoutManager =  LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
//                addItemDecoration(DividerItemDecoration(context, orientation))
            }
        }

        override fun createView(parent: ViewGroup, viewType: Int): SearchResultView {
            return SearchResultView(context)
        }

        override fun bind(item: SearchResult, view: SearchResultView) {
            view.setResult(item)
        }

        override fun isSameItem(a: SearchResult, b: SearchResult): Boolean = a.id == b.id
    }

    class SearchResultView(context: Context) : CustomView(context) {

        init {
            cardTheme()
        }

        private val cover = ImageView(context).apply {
            adjustViewBounds = true
        }

        private val artistName = TextView(context).apply {
            maxLines = 1
            setTextSizeSp(20F)

        }
        private val albumName = TextView(context).apply {
            maxLines = 1
            setTextSizeSp(20F)

        }
        private val details = TextView(context).apply {
            maxLines = 1
            setTextSizeSp(14F)

        }

        override fun onInitializeLayout() {
            super.onInitializeLayout()
            contourHeightOf {
                maxOf(cover.bottom(), details.bottom())
            }
            setMargin(Theme.Dimensions.standardMargin.xdip, Theme.Dimensions.standardMargin.ydip / 2)
            // TODO: contour doesn't seem to support this style of square layouts, because
            // it doesn't make a distinction between x- and y- dependencies for determining
            // circular references...
            // https://github.com/cashapp/contour/issues/14
            cover.applyLayout(
                x = toParentLeft().widthOf { 100.xdip },
                y = toParentTop().heightOf { 100.ydip }
            )
            artistName.applyLayout(
                x = rightOf(cover).toParentRight().withDefaultMargin(),
                y = toParentTop()
            )
            albumName.applyLayout(
                x = rightOf(cover).toParentRight().withDefaultMargin(),
                y = below(artistName)
            )
            details.applyLayout(
                x = rightOf(cover).toParentRight().withDefaultMargin(),
                y = below(albumName)
            )
        }

        fun setResult(searchResult: SearchResult) {
            searchResult.coverUrl.coverImageLoader().into(cover)
            artistName.text = searchResult.artistName
            albumName.text = searchResult.albumName
        }
    }
}
