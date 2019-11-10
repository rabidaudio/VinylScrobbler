package audio.rabid.vinylscrobbler.data.lastfm

class LastFMUnauthenticatedException(method: String) :
    IllegalStateException("Tried to use signed method $method but sessionKey was not set")
