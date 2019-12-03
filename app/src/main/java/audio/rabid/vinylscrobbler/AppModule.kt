package audio.rabid.vinylscrobbler

import audio.rabid.kaddi.dsl.module
import audio.rabid.vinylscrobbler.data.DataModule

val AppModule = module("App") {
    import(DataModule)
    import(DebuggingModule)
}
