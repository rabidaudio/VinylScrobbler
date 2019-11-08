package audio.rabid.vinylscrobbler.lastfm

class LastFMUnauthenticatedException(method: String) :
    IllegalStateException("Tried to use signed method $method but sessionKey was not set")
