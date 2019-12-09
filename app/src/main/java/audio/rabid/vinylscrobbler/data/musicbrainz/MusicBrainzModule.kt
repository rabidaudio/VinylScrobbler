package audio.rabid.vinylscrobbler.data.musicbrainz

import audio.rabid.kaddi.dsl.bind
import audio.rabid.kaddi.dsl.module
import audio.rabid.kaddi.dsl.require
import audio.rabid.kaddi.dsl.withSingleton
import audio.rabid.kaddi.instance
import okhttp3.OkHttpClient
import retrofit2.Retrofit


val MusicBrainzModule = module("MusicBrainz") {

    require<Retrofit.Builder>()
    require<OkHttpClient>()
    bind<MusicBrainzApi>().withSingleton {
        MusicBrainzApi.create(retrofitBuilder = instance(),  okHttpClient = instance())
    }
}
