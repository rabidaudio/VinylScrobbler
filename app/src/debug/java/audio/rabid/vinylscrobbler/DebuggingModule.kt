package audio.rabid.vinylscrobbler

import audio.rabid.kaddi.dsl.module

val DebuggingModule = module("Debugging") {

    import(FlipperModule)

    // bindIntoSet<Interceptor>().with { OkReplayInterceptor() }
}
