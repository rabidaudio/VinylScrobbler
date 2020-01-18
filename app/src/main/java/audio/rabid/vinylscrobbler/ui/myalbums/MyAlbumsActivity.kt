package audio.rabid.vinylscrobbler.ui.myalbums

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable.LINEAR_GRADIENT
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import audio.rabid.kaddi.Kaddi
import audio.rabid.kaddi.inject
import audio.rabid.vinylscrobbler.R
import audio.rabid.vinylscrobbler.core.ui.*
import audio.rabid.vinylscrobbler.data.db.models.Album
import audio.rabid.vinylscrobbler.ui.addalbum.AddAlbumActivity
import audio.rabid.vinylscrobbler.ui.coverImageLoader
import com.google.android.material.floatingactionbutton.FloatingActionButton
import group.infotech.drawable.dsl.shapeDrawable

class MyAlbumsActivity : AppCompatActivity() {

    companion object {
        private const val MENU_ITEM_SEARCH = 0
        private const val MENU_ITEM_LOGOUT = 1
    }

    private val myAlbumsView by bindView(::MyAlbumsView)

    private val viewModel by inject<MyAlbumsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(myAlbumsView)

        Kaddi.getScope(applicationContext)
            .createChildScope(this, MyAlbumsModule)
            .inject(this)

        setSupportActionBar(myAlbumsView.toolbar)
        myAlbumsView.fab.setOnClickListener {
            navigateToAddAlbum()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE, MENU_ITEM_SEARCH, Menu.NONE, "Search").apply {
            setIcon(R.drawable.ic_search_dark_tinted)
            setShowAsAction(SHOW_AS_ACTION_IF_ROOM)
        }
        menu.add(Menu.NONE, MENU_ITEM_LOGOUT, Menu.NONE, "Sign out of Last.FM")

        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.observe(this, this::onStateChanged)
    }

    private fun onStateChanged(state: MyAlbumsViewModel.State) {
        myAlbumsView.albumGrid.setItems(state.albums)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            MENU_ITEM_SEARCH -> Unit // TODO search my albums
            MENU_ITEM_LOGOUT -> Unit // TODO
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onDestroy() {
        Kaddi.closeScope(this)
        super.onDestroy()
    }

    private fun navigateToAddAlbum() {
        startActivity(Intent(this, AddAlbumActivity::class.java))
    }

    private class MyAlbumsView(context: Context) : CustomView(context) {

        val toolbar = Toolbar(context).apply {
            applyLayout(
                x = matchParentX(),
                y = toParentTop()
            )
        }

        val albumGrid = AlbumGridView(context).apply {
            applyLayout(
                x = matchParentX(),
                y = below(toolbar).toParentBottom()
            )
        }

        val fab = FloatingActionButton(context).apply {
            setImageResource(R.drawable.ic_add)
            applyLayout(
                x = rightTo { parent.right() - theme.dimensions.fabMargin.toXInt() },
                y = bottomTo { parent.bottom() - theme.dimensions.fabMargin.toYInt() }
            )
        }
    }

    class AlbumGridView(context: Context) : BindingRecyclerView<Album, AlbumView>(context) {

        init {
            val columns = maxOf(screenWidthDip / 200, 1) // shoot for making albums about 200 dp
            layoutManager =  GridLayoutManager(context, columns)
        }

        override fun createView(parent: ViewGroup, viewType: Int): AlbumView {
            return AlbumView(context)
        }

        override fun bind(item: Album, view: AlbumView) {
            view.setAlbum(item)
        }

        override fun isSameItem(a: Album, b: Album): Boolean = a == b
    }

    class AlbumView(context: Context) : CustomView(context) {
        init {
            contourHeightOf {
                cover.bottom()
            }
        }

        private val cover = ImageView(context).apply {
            adjustViewBounds = true
            applyLayout(
                x = matchParentX(),
                // force square. note: ideally this would be width().toY() instead of parent, but
                // currently this causes a circular reference error
                y = topTo { parent.top() }.heightOf { parent.width().toY() }
            )
        }

        private val skrim = View(context).apply {
            background = shapeDrawable {
                gradientType = LINEAR_GRADIENT
                colors = intArrayOf(Color.TRANSPARENT, Color.BLACK.withAlpha(0.5F))
            }
            applyLayout(
                x = matchParentX(),
                y = toParentBottom().heightOf { parent.height() }
            )
        }

        private val albumName = TextView(context).apply {
            setTextAppearance(R.style.AppTheme_TextAppearance_H2)
            setTextColor(theme.colors.accentDark.onColor)
            applyLayout(
                x = matchParentX().withDefaultMargin(),
                y = bottomTo { artistName.top() } //.withDefaultMargin()
            )
        }

        private val artistName = TextView(context).apply {
            setTextAppearance(R.style.AppTheme_TextAppearance_H2)
            setTextColor(theme.colors.accentDark.onColor)
            applyLayout(
                x = matchParentX().withDefaultMargin(),
                y = bottomTo { releaseYear.top() } //.withDefaultMargin()
            )
        }

        private val releaseYear = TextView(context).apply {
            setTextColor(theme.colors.accentDark.onColor)
            applyLayout(
                x = matchParentX().withDefaultMargin(),
                y = toParentBottom() //.withDefaultMargin()
            )
        }

        fun setAlbum(album: Album) {
            album.coverUrl.coverImageLoader().into(cover)
            if (album.coverUrl == null) {
                albumName.text = album.name
                artistName.text = album.artistName
                releaseYear.text = "2006" // STOPSHIP
                skrim.visibility = View.VISIBLE
                albumName.visibility = View.VISIBLE
                artistName.visibility = View.VISIBLE
                releaseYear.visibility = View.VISIBLE
            } else {
                skrim.visibility = View.GONE
                albumName.visibility = View.GONE
                artistName.visibility = View.GONE
                releaseYear.visibility = View.GONE
            }
        }
    }
}
