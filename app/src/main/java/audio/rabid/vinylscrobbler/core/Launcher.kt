package audio.rabid.vinylscrobbler.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import audio.rabid.kaddi.BindingKey
import audio.rabid.kaddi.JustProvider
import audio.rabid.kaddi.Module
import audio.rabid.kaddi.dsl.BindingBlock
import audio.rabid.kaddi.dsl.module
import java.io.Serializable

/**
 * The idea here is to standardize passing arguments into and out of intents when starting Activities.
 * An activity can set it's companion object to subclass [Launcher.Factory] and specify a
 * [Serializable] data class to hold it's arguments in. That way it's less likely to mess up by
 * passing the wrong arguments to the Activity.
 */
class Launcher<C : Parcelable> private constructor(
    private val activityClass: Class<out Activity>,
    private val data: C
) {
    companion object {
        private const val EXTRA_ARGUMENTS = "EXTRA_ARGUMENTS"
    }

    abstract class Factory<C : Parcelable>(
        private val activityClass: Class<out Activity>
    ) {
        fun getLauncher(data: C): Launcher<C> = Launcher(activityClass, data)

        @Suppress("UNCHECKED_CAST")
        fun getArguments(intent: Intent): C {
            return intent.getParcelableExtra(EXTRA_ARGUMENTS) as? C
                ?: throw IllegalStateException("Arguments not found")
        }
    }

    fun getLaunchIntent(context: Context): Intent =
        Intent(context, activityClass).also { it.putExtra(EXTRA_ARGUMENTS, data) }

    fun startActivity(context: Context) {
        context.startActivity(getLaunchIntent(context))
    }

    fun startActivityForResult(context: Activity, requestCode: Int) {
        context.startActivityForResult(getLaunchIntent(context), requestCode)
    }
}

inline fun <reified C : Parcelable> Launcher.Factory<C>.getArgumentsModule(intent: Intent): Module {
    return module("Arguments:${intent.hashCode()}") {
        // TODO: add a bind-raw method
        BindingBlock.bind(this, BindingKey(C::class, Unit), overrides = false, intoSet = false)
            .with(JustProvider(getArguments(intent)))
    }
}
