package audio.rabid.vinylscrobbler

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.squareup.contour.ContourLayout
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.HttpUrl

class MyAlbumsActivity : AppCompatActivity() {

    private val albumGrid by lazy { AlbumGridView(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(albumGrid)
    }

    override fun onStart() {
        super.onStart()
        val albumDao = AppDatabase.get(this).albumDao()
        // TODO: this is temporary until we set up a view model
        GlobalScope.launch {
            val albums = albumDao.getAll()
            runOnUiThread {
                albumGrid.setItems(albums)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
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

    class AlbumView(context: Context, attrs: AttributeSet? = null) : ContourLayout(context, attrs) {
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
            setCoverImage(album.coverUrl)
        }

        private fun setCoverImage(url: HttpUrl?) {
            with(Picasso.get()) {
                url?.let { load(it.toString()) } ?: load(R.drawable.cover2)
            }.resize(500, 500)
                .onlyScaleDown()
                .centerCrop()
                .into(cover)
        }
    }
}
