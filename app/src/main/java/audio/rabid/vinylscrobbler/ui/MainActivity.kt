package audio.rabid.vinylscrobbler.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import audio.rabid.vinylscrobbler.ui.addalbum.AddAlbumActivity
import audio.rabid.vinylscrobbler.ui.myalbums.MyAlbumsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STOPSHIP
        startActivity(Intent(this, AddAlbumActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }
}
