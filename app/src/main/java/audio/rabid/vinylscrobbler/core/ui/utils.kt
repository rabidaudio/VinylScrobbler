@file:Suppress("NOTHING_TO_INLINE")
package audio.rabid.vinylscrobbler.core.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.graphics.ColorUtils
import androidx.core.view.updateLayoutParams
import audio.rabid.vinylscrobbler.R
import kotlin.math.roundToInt

inline fun View.setPadding(x: Int, y: Int) {
    setPadding(x, y, x, y)
}

inline fun View.setPadding(padding: Int) {
    setPadding(padding, padding, padding, padding)
}

inline fun TextView.setTextSizeSp(value: Float) {
    setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
}

inline val View.density: Float get() = context.resources.displayMetrics.density
inline val View.screenWidth: Int get() = context.resources.displayMetrics.widthPixels
inline val View.screenHeight: Int get() = context.resources.displayMetrics.heightPixels
inline val View.screenWidthDip: Int get() = (screenWidth.toFloat() / density).roundToInt()
inline val View.screenHeightDip: Int get() = (screenHeight.toFloat() / density).roundToInt()

inline fun <V: View> Activity.bindView(crossinline ctor: (Context) -> V,
                                       noinline apply: (V.() -> Unit)? = null): Lazy<V> {
    return lazy { ctor.invoke(this).also { view -> apply?.invoke(view) } }
}

@ColorInt
fun View.getColor(@ColorRes colorId: Int): Int = context.resources.getColor(colorId, context.theme)

inline fun View.getString(@StringRes stringId: Int, vararg formatArgs: Any)
        = context.resources.getString(stringId, *formatArgs)

@ColorInt
fun @receiver:ColorInt Int.withAlpha(alpha: Float): Int =
    Color.argb((alpha * 255).roundToInt(), Color.red(this), Color.green(this), Color.blue(this))
