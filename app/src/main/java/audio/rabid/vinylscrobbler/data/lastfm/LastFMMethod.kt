package audio.rabid.vinylscrobbler.data.lastfm

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LastFMMethod(
    val method: String,
    val authenticated: Boolean = false,
    val signed: Boolean = false
)
