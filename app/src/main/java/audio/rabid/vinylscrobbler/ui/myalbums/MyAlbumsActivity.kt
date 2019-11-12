package audio.rabid.vinylscrobbler.ui.myalbums

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import audio.rabid.vinylscrobbler.R
import audio.rabid.vinylscrobbler.core.ui.BindingRecyclerView
import audio.rabid.vinylscrobbler.core.ui.bindView
import audio.rabid.vinylscrobbler.core.ui.matchParentX
import audio.rabid.vinylscrobbler.core.ui.screenWidthDip
import audio.rabid.vinylscrobbler.data.models.Album
import com.squareup.contour.ContourLayout
import com.squareup.picasso.Picasso
import okhttp3.HttpUrl
import javax.inject.Inject

class MyAlbumsActivity : AppCompatActivity() {

    private val albumGrid by bindView(::AlbumGridView)

//    private val viewModel by inject<MyAlbumsViewModel>()

//    @Subcomponent
//    interface Compontent : AndroidInjector<MyAlbumsActivity> {
////        fun viewModel(): MyAlbumsViewModel
//
//        @Subcomponent.Factory
//        interface Factory : AndroidInjector.Factory<MyAlbumsActivity>
//    }

    @Inject
    lateinit var viewModel: MyAlbumsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(albumGrid)

//        KTP.openScopes(ApplicationScope::class.java, this)
//            .supportScopeAnnotation(ActivityScope::class.java)
//            .installModules(SmoothieActivityModule(this), MyAlbumsModule)
//            .closeOnDestroy(this)
//            .inject(this)
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
