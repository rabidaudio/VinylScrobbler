package audio.rabid.vinylscrobbler.lastfm

interface LastFMSessionManager {
    fun setSession(session: AuthGetMobileSessionResponse)

    fun clearSession()
}
