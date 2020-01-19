package audio.rabid.vinylscrobbler.ui.myalbums

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import audio.rabid.kaddi.Kaddi
import audio.rabid.kaddi.inject
import audio.rabid.vinylscrobbler.R
import audio.rabid.vinylscrobbler.core.ui.ViewBindingAdapter
import audio.rabid.vinylscrobbler.core.ui.screenWidthDip
import audio.rabid.vinylscrobbler.core.ui.viewBinding
import audio.rabid.vinylscrobbler.data.db.models.Album
import audio.rabid.vinylscrobbler.databinding.ActivityMyAlbumsBinding
import audio.rabid.vinylscrobbler.databinding.SquareAlbumViewBinding
import audio.rabid.vinylscrobbler.ui.addalbum.AddAlbumActivity
import audio.rabid.vinylscrobbler.ui.coverImageLoader

class MyAlbumsActivity : AppCompatActivity() {

    companion object {
        private const val MENU_ITEM_SEARCH = 0
        private const val MENU_ITEM_LOGOUT = 1
    }

    private val viewModel by inject<MyAlbumsViewModel>()
    private val views by viewBinding(ActivityMyAlbumsBinding::inflate)
    private val myAlbumsAdapter = AlbumListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Kaddi.getScope(applicationContext)
            .createChildScope(this, MyAlbumsModule)
            .inject(this)

        setSupportActionBar(views.Toolbar)
        views.Fab.setOnClickListener {
            navigateToAddAlbum()
        }
        with(views.MyAlbumsList) {
            val columns = maxOf(screenWidthDip / 200, 1) // shoot for making albums about 200 dp
            layoutManager = GridLayoutManager(context, columns)
            adapter = myAlbumsAdapter
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
//        myAlbumsView.albumGrid.setItems(state.albums)
        myAlbumsAdapter.setItems(state.albums)
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

    class AlbumListAdapter :
        ViewBindingAdapter<Album, SquareAlbumViewBinding>(SquareAlbumViewBinding::inflate) {
        override fun isSameItem(a: Album, b: Album): Boolean = a == b

        override fun bind(item: Album, viewBinding: SquareAlbumViewBinding) {
            viewBinding.setAlbum(item)
        }
    }
}

fun SquareAlbumViewBinding.setAlbum(album: Album) {
    album.coverUrl.coverImageLoader().placeholder(R.drawable.ic_album_with_skrim).into(Cover)
    if (album.coverUrl == null) {
        Title.text = album.name
        // artistName.text = album.artistName
        ReleaseYear.text = "2006" // STOPSHIP
//        Skrim.visibility = View.VISIBLE
        ReleaseYear.visibility = View.VISIBLE
        // artistName.visibility = View.VISIBLE
        ReleaseYear.visibility = View.VISIBLE
    } else {
//        Skrim.visibility = View.GONE
        Title.visibility = View.GONE
        // artistName.visibility = View.GONE
        ReleaseYear.visibility = View.GONE
    }
}