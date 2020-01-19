package audio.rabid.vinylscrobbler.ui

import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import okhttp3.HttpUrl

fun HttpUrl?.coverImageLoader(): RequestCreator {
    return Picasso.get()
        .load(this?.toString())
        .resize(500, 500)
        .onlyScaleDown()
        .centerCrop()
}
