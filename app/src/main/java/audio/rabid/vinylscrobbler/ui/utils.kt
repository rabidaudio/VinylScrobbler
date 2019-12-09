package audio.rabid.vinylscrobbler.ui

import audio.rabid.vinylscrobbler.R
import audio.rabid.vinylscrobbler.data.db.models.Album
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import okhttp3.HttpUrl

fun HttpUrl?.coverImageLoader(): RequestCreator {
    return Picasso.get()
        .load(this?.toString())
        .placeholder(R.drawable.ic_album_tinted)
        .resize(500, 500)
        .onlyScaleDown()
        .centerCrop()
}
