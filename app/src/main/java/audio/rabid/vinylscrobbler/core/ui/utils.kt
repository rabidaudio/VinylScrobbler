@file:Suppress("NOTHING_TO_INLINE")
package audio.rabid.vinylscrobbler.core.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.cardview.widget.CardView
import audio.rabid.vinylscrobbler.R
import com.arlib.floatingsearchview.FloatingSearchView
import kotlin.math.roundToInt

inline val View.density: Float get() = context.resources.displayMetrics.density
inline val View.screenWidth: Int get() = context.resources.displayMetrics.widthPixels
inline val View.screenHeight: Int get() = context.resources.displayMetrics.heightPixels
inline val View.screenWidthDip: Int get() = (screenWidth.toFloat() / density).roundToInt()
inline val View.screenHeightDip: Int get() = (screenHeight.toFloat() / density).roundToInt()

inline fun <V: View> Activity.bindView(crossinline ctor: (Context) -> V): Lazy<V> {
    return lazy { ctor.invoke(this) }
}

@ColorInt
fun View.getColor(@ColorRes colorId: Int): Int = context.resources.getColor(colorId, context.theme)

inline fun View.getString(@StringRes stringId: Int, vararg formatArgs: Any)
        = context.resources.getString(stringId, *formatArgs)

@ColorInt
fun @receiver:ColorInt Int.withAlpha(alpha: Float): Int =
    Color.argb((alpha * 255).roundToInt(), Color.red(this), Color.green(this), Color.blue(this))

inline fun Context.withStyle(@StyleRes styleId: Int) = ContextThemeWrapper(this, styleId)
