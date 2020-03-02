package audio.rabid.vinylscrobbler.ui.addalbum.add

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import audio.rabid.kaddi.Kaddi
import audio.rabid.kaddi.inject
import audio.rabid.vinylscrobbler.R
import audio.rabid.vinylscrobbler.core.Launcher
import audio.rabid.vinylscrobbler.core.getArgumentsModule
import audio.rabid.vinylscrobbler.core.ui.ViewBindingAdapter
import audio.rabid.vinylscrobbler.core.ui.viewBinding
import audio.rabid.vinylscrobbler.data.musicbrainz.MusicBrainz
import audio.rabid.vinylscrobbler.databinding.ActivityAddReleaseBinding
import audio.rabid.vinylscrobbler.databinding.ItemReleaseBinding
import audio.rabid.vinylscrobbler.ui.addalbum.add.AddReleaseViewModel.ReleaseResult
import audio.rabid.vinylscrobbler.ui.coverImageLoader
import kotlinx.android.parcel.Parcelize
import java.net.URI
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class AddReleaseActivity : AppCompatActivity() {

    @Parcelize
    data class Arguments(
        val releaseGroupId: UUID,
        val coverUrl: URI?,
        val artistName: String,
        val albumName: String
    ): Parcelable

    companion object : Launcher.Factory<Arguments>(
        AddReleaseActivity::class.java)

    private val viewModel by inject<AddReleaseViewModel>()
    private val views by viewBinding(ActivityAddReleaseBinding::inflate)
    private val releasesAdapter = ReleasesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Kaddi.getScope(application)
            .createChildScope(this, getArgumentsModule(intent), AddReleaseModule)
            .inject(this)

        with(views.ReleaseList) {
            adapter = releasesAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.observe(this) { state ->
            state.coverArtUrl.coverImageLoader()
                .placeholder(R.drawable.ic_album_with_skrim)
                .into(views.ToolbarSplashImage)

            // TODO: use separate text boxes
            views.ToolbarTitle.text = "${state.artistName}\n${state.artistName}"

            if (state.releases == null) {
                views.ReleasesLoading.visibility = View.VISIBLE
                views.ReleaseList.visibility = View.INVISIBLE
            } else {
                releasesAdapter.setItems(state.releases)
                views.ReleasesLoading.visibility = View.GONE
                views.ReleaseList.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        Kaddi.closeScope(this)
        super.onDestroy()
    }

    private inner class ReleasesAdapter
        : ViewBindingAdapter<ReleaseResult, ItemReleaseBinding>(ItemReleaseBinding::inflate) {

        override fun bind(item: ReleaseResult, viewBinding: ItemReleaseBinding) {
            viewBinding.setResult(item)
        }

        override fun isSameItem(a: ReleaseResult, b: ReleaseResult): Boolean = a.releaseId == b.releaseId

        override fun onItemClick(item: ReleaseResult, viewBinding: ItemReleaseBinding) {
            viewModel.addRelease(item)
        }
    }

    fun ItemReleaseBinding.setResult(searchResult: ReleaseResult) {
        Format.text = searchResult.format
        ReleaseDate.text = searchResult.releaseDate?.let {
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(it)
        }
        ReleaseDate.visibility = if (searchResult.releaseDate == null) View.GONE else View.VISIBLE
        Label.text = listOfNotNull(searchResult.label, searchResult.country).joinToString(" | ")
        if (searchResult.label == null && searchResult.country == null) {
            Label.visibility = View.GONE
        } else {
            Label.visibility = View.VISIBLE
        }
        TrackListing.setTrackListing(searchResult.tracks.toTrackListing())
    }

    private fun List<MusicBrainz.Track>.toTrackListing(): List<TrackListingView.Track> {
        return map { track ->
            TrackListingView.Track(
                position = track.number,
                title = track.title,
                duration = track.duration
            )
        }
    }
}
