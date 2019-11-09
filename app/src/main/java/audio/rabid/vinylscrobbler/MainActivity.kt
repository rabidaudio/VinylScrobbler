package audio.rabid.vinylscrobbler

import android.content.Context
import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, MyAlbumsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }
}
