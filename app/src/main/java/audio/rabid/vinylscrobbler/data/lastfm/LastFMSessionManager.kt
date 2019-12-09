package audio.rabid.vinylscrobbler.data.lastfm

interface LastFMSessionManager {
    fun setSession(session: LastFM.AuthGetMobileSessionResponse)

    fun clearSession()
}
