package audio.rabid.vinylscrobbler.data.lastfm

interface LastFMSessionManager {
    fun setSession(session: AuthGetMobileSessionResponse)

    fun clearSession()
}
