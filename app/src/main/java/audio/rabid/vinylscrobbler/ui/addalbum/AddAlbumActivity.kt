package audio.rabid.vinylscrobbler.ui.addalbum

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.setPadding
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import audio.rabid.kaddi.Kaddi
import audio.rabid.kaddi.inject
import audio.rabid.vinylscrobbler.R
import audio.rabid.vinylscrobbler.core.ui.*
import audio.rabid.vinylscrobbler.ui.addalbum.AddAlbumViewModel.SearchResult
import audio.rabid.vinylscrobbler.ui.coverImageLoader
import audio.rabid.vinylscrobbler.ui.theme
import com.arlib.floatingsearchview.FloatingSearchView

class AddAlbumActivity : AppCompatActivity(),
    FloatingSearchView.OnQueryChangeListener,
    FloatingSearchView.OnHomeActionClickListener,
    BindingRecyclerView.OnItemClickListener<SearchResult, AddAlbumActivity.SearchResultView> {

    private val albumView by bindView(::AddAlbumView)

    private val viewModel by inject<AddAlbumViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(albumView)
//        setContentView(SearchToolbar(this))

        Kaddi.getScope(application)
            .createChildScope(this, AddAlbumModule)
            .inject(this)

//        albumView.searchView.setOnQueryChangeListener(this)
//        albumView.searchView.setOnHomeActionClickListener(this)
        albumView.searchResults.setOnItemClickListener(this)
        albumView.searchResults.setOnEndlessScrollLoadMoreCallback(rowThreshold = 3) {
            // TODO load next page of results
        }
        intent.setQuery()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.setQuery()
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.observe(this) { state ->
            albumView.searchResults.setItems(state.results)
        }
    }

    override fun onSearchTextChanged(oldQuery: String, newQuery: String) {
        viewModel.search(newQuery)
    }

    override fun onItemClick(
        view: SearchResultView,
        item: SearchResult,
        adapter: RecyclerView.Adapter<BindingRecyclerView.SimpleViewHolder<SearchResultView>>
    ) {
        // TODO: navigate to album details
    }

    override fun onHomeClicked() {
        finish()
    }

    override fun onDestroy() {
        Kaddi.closeScope(this)
        super.onDestroy()
    }

    private fun Intent.setQuery() {
        if (action == Intent.ACTION_SEARCH) {
            albumView.searchView.setSearchText(getStringExtra(SearchManager.QUERY))
        }
    }

    private class SearchToolbar(context: Context) : CustomView(context) {

        init {
            contourHeightOf { 56.ydip }
        }

        val backArrow = ImageView(context).apply {
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(R.drawable.ic_arrow_back)
            applyLayout(
                x = leftTo { parent.left() + 16.xdip }.widthOf { 24.xdip },
                y = centerVerticallyTo { parent.centerY() }.heightOf { 24.ydip }
            )
        }

        val searchBox = EditText(context).apply {
            setHint(R.string.search)
            background = null
            applyLayout(
                x = leftTo { backArrow.right() + 32.xdip }.rightTo { parent.right() - 16.xdip },
                y = centerVerticallyTo { parent.centerY() }
            )
        }

        fun setSearchText(query: String?) {
            searchBox.setText(query)
            query ?: return
        }

        override fun onInitializeLayout() {
            super.onInitializeLayout()

        }
    }

    private class AddAlbumView(context: Context) : CustomView(context) {
        init {
            setBackgroundColor(theme.colors.background.color)
        }

        val searchView = SearchToolbar(context).apply {
            applyLayout(
                x = toParentLeft(),
                y = toParentTop()
            )
        }

//        val searchView = FloatingSearchView(context).apply {
//            // FloatingSearchView doesn't support themes not through XML so we have to theme
//            // programmatically
//            setSearchHint(getString(R.string.search))
//            setLeftActionMode(FloatingSearchView.LEFT_ACTION_MODE_SHOW_HOME)
//            setCloseSearchOnKeyboardDismiss(true)
//            elevation = 16.dip.toFloat()
//            // FloatingSearchView has some broken background styles so we have to cheat and set
//            // one of the backgrounds to transparent
////            setBackgroundColor(theme.colors.surface.color)
////            findViewById<CardView>(R.id.search_query_section).setBackgroundColor(Color.TRANSPARENT)
////            setBackgroundColor(Color.TRANSPARENT)
//            findViewById<View>(R.id.search_suggestions_section).visibility = View.GONE
//////            setAllForegroundColors(theme.colors.surface.onColor)
////            setViewTextColor(theme.colors.surface.onColor)
////            setLeftActionIconColor(theme.colors.surface.onColor)
////            setMenuItemIconColor(theme.colors.surface.onColor)
////            setHintTextColor(theme.colors.surface.onColor)
////            setClearBtnColor(theme.colors.surface.onColor)
//            applyLayout(
//                x = matchParentX(), //.withDefaultMargin()
//                y = toParentTop() // .withHalfMargin()
//            )
//        }

        val searchResults = SearchResultsView(context).apply {
//            applyLayout(
//                x = matchParentX(),
//                y = below(searchView).toParentBottom() //.bottomTo { parent.bottom() - standardMarginY / 2 } //.withHalfMargin() // matchParentY() //
//            )
        }
    }

    class SearchResultsView(context: Context) :
        BindingRecyclerView<SearchResult, SearchResultView>(context) {
        init {
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
//                addItemDecoration(DividerItemDecoration(context, orientation))
            }
            setBackgroundColor(context.theme().colors.background.color)
        }

        override fun createView(parent: ViewGroup, viewType: Int): SearchResultView {
            return SearchResultView(context)
        }

        override fun bind(item: SearchResult, view: SearchResultView) {
            view.setResult(item)
        }

        override fun isSameItem(a: SearchResult, b: SearchResult): Boolean = a.id == b.id
    }

    class SearchResultView(context: Context) : CustomView(context, R.style.AppTheme_Card) {
        // TODO: contour doesn't seem to support this style of square layouts, because
        // it doesn't make a distinction between x- and y- dependencies for determining
        // circular references...
        // https://github.com/cashapp/contour/issues/14
        private val coverSize = 100.dip

        init {
            clipToOutline = true
        }

        private val cover = ImageView(context).apply {
            adjustViewBounds = true
        }

        private val artistName = TextView(context).apply {
            setTextAppearance(R.style.AppTheme_TextAppearance_H2)
        }

        private val albumName = TextView(context).apply {
            setTextAppearance(R.style.AppTheme_TextAppearance_H2)
        }

        private val details = TextView(context)

        override fun onInitializeLayout() {
            super.onInitializeLayout()
            contourHeightOf {
                maxOf(cover.bottom(), details.bottom())
            }
            setMargin(
                x = theme.dimensions.standardMargin.toXInt(),
                y = theme.dimensions.standardMargin.toYInt() / 2
            )

            cover.applyLayout(
                x = toParentLeft().widthOf { coverSize.toXInt() },
                y = toParentTop().heightOf { coverSize.toYInt() }
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
            details.text = searchResult.releaseDate.year.toString()
        }
    }
}
