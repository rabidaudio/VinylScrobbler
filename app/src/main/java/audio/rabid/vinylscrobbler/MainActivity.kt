package audio.rabid.vinylscrobbler

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.squareup.contour.*
import com.squareup.picasso.Picasso
import okhttp3.HttpUrl

class MainActivity : AppCompatActivity() {

    private val view by lazy { AlbumGridView(this) }

    val albums = List(50) { Album("foo", "bar", null) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        view.setItems(albums)
    }
}

class AlbumGridView(context: Context) : BindingRecyclerView<Album, AlbumView>(context) {

    init {
        val columns = maxOf(screenWidthDip / 150, 1) // shoot for making albums about 150 dp
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

data class Album(val name: String, val artistName: String, val cover: HttpUrl?)

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
            // force square. note: ideally this would be
            y = topTo { parent.top() }.heightOf { parent.width().toY() } // force square
        )
    }

    fun setAlbum(album: Album) {
        setCoverImage(album.cover)
    }

    private fun setCoverImage(url: HttpUrl?) {
        with(Picasso.get()) {
            url?.let { load(it.toString()) } ?: load(R.drawable.cover2)
        }.placeholder(R.drawable.cover2)
            .resize(500, 500)
            .onlyScaleDown()
            .centerCrop()
            .into(cover)
    }
}
