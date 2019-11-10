@file:Suppress("NOTHING_TO_INLINE")
package audio.rabid.vinylscrobbler.core.ui

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.TextView
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

inline fun <V: View> Activity.bindView(crossinline ctor: (Context) -> V) = lazy { ctor.invoke(this) }
