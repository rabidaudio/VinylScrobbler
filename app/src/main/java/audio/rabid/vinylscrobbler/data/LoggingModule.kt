package audio.rabid.vinylscrobbler.data

import audio.rabid.kaddi.dsl.*
import audio.rabid.kaddi.instance
import audio.rabid.kaddi.setInstance
import com.fixdapp.android.logger.LogManager
import com.fixdapp.android.logger.Logger
import com.fixdapp.android.logger.LoggerEndpoint

val LoggingModule  = module("Logging") {

    declareSetBinding<LoggerEndpoint>("LoggerEndpoints")

    bind<LogManager>().withSingleton {
        LogManager(setInstance<LoggerEndpoint>("LoggerEndpoints").toTypedArray(), emptyArray())
    }

    bind<Logger>().with { instance<LogManager>().newLogger }
}
