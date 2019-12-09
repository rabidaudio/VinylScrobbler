package audio.rabid.vinylscrobbler.ui.myalbums

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import audio.rabid.kaddi.Kaddi
import audio.rabid.kaddi.inject
import audio.rabid.vinylscrobbler.R
import audio.rabid.vinylscrobbler.core.ui.BindingRecyclerView
import audio.rabid.vinylscrobbler.core.ui.CustomView
import audio.rabid.vinylscrobbler.core.ui.bindView
import audio.rabid.vinylscrobbler.core.ui.screenWidthDip
import audio.rabid.vinylscrobbler.data.db.models.Album
import audio.rabid.vinylscrobbler.ui.coverImageLoader

class MyAlbumsActivity : AppCompatActivity() {

    private val albumGrid by bindView(::AlbumGridView)

    private val viewModel by inject<MyAlbumsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(albumGrid)

        Kaddi.getScope(applicationContext)
            .createChildScope(this, MyAlbumsModule)
            .inject(this)
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.observe(this, this::onStateChanged)
    }

    private fun onStateChanged(state: MyAlbumsViewModel.State) {
        albumGrid.setItems(state.albums)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        // TODO build menus in code instead of xml
        menuInflater.inflate(R.menu.menu_my_albums, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                // TODO go to add activity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class AlbumGridView(context: Context) : BindingRecyclerView<Album, AlbumView>(context) {

        init {
            val columns = maxOf(screenWidthDip / 120, 1) // shoot for making albums about 120 dp
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

        fun setAlbum(album: Album) {
            album.coverUrl.coverImageLoader().into(cover)
        }
    }
}
