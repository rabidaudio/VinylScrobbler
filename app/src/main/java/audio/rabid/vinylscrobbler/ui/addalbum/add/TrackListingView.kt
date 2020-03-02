package audio.rabid.vinylscrobbler.ui.addalbum.add

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import audio.rabid.vinylscrobbler.databinding.ItemTrackBinding
import java.time.Duration

class TrackListingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    data class Track(
        val position: String,
        val title: String,
        val duration: Duration
    )

    companion object {
        private val SAMPLE_DATA = listOf(
            Track("A1", "Too Small for Eyes", Duration.ofSeconds(347)),
            Track("A2", "It Hurts Until It Doesn’t", Duration.ofSeconds(334)),
            Track("A3", "Copper Mines", Duration.ofSeconds(237)),
            Track("A4", "Nesting Behavior", Duration.ofSeconds(377)),
            Track("B1", "Lockjaw", Duration.ofSeconds(319)),
            Track("B2", "Blood‐letting", Duration.ofSeconds(337)),
            Track("B3", "Burden of Proof", Duration.ofSeconds(183)),
            Track("B4", "Hold Your Own Hand", Duration.ofSeconds(5714))
        )
    }

    init {
        orientation = VERTICAL
        // Useful for the previewer. will get compiled out of real builds
        if (isInEditMode) setTrackListing(SAMPLE_DATA)
    }

    private var tracks: List<Track> = emptyList()

    fun setTrackListing(tracks: List<Track>) {
        if (tracks == this.tracks) return
        removeAllViews()
        val layoutInflater = LayoutInflater.from(context)
        for (track in tracks) {
            ItemTrackBinding.inflate(layoutInflater, this, false).apply {
                Position.text = track.position
                Title.text = track.title
                Length.text = track.duration.getLocalizedString(context)
                addView(root)
            }
        }
        this.tracks = tracks
    }
}

fun Duration.getLocalizedString(context: Context): String {
    // TODO: this isn't localized...
    val h = toHours()
    val m = minusHours(toHours()).toMinutes()
    val s = minusHours(toHours()).let { it.minusMinutes(it.toMinutes()) }.seconds
    return if (h == 0L) String.format("%d:%02d", m, s) else String.format("%d:%02d:%02d", h, m, s)
}
