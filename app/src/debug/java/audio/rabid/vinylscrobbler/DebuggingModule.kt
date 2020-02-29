package audio.rabid.vinylscrobbler

import audio.rabid.kaddi.dsl.bindIntoSet
import audio.rabid.kaddi.dsl.module
import audio.rabid.kaddi.dsl.with
import audio.rabid.kaddi.instance
import audio.rabid.vinylscrobbler.data.LoggingInterceptor
import audio.rabid.vinylscrobbler.data.LoggingModule
import com.fixdapp.android.logger.DebugLoggerEndpoint
import com.fixdapp.android.logger.LoggerEndpoint
import okhttp3.Interceptor

val DebuggingModule = module("Debugging") {
    import(LoggingModule)
    import(FlipperModule)

    bindIntoSet<LoggerEndpoint>("LoggerEndpoints").with { DebugLoggerEndpoint() }
    bindIntoSet<Interceptor>().with { LoggingInterceptor(instance()) }
    // bindIntoSet<Interceptor>().with { OkReplayInterceptor() }
}
