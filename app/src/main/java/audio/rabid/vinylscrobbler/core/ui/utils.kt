@file:Suppress("NOTHING_TO_INLINE")

package audio.rabid.vinylscrobbler.core.ui

import android.content.Context
import kotlin.math.roundToInt

inline val Context.density: Float get() = resources.displayMetrics.density
inline val Context.screenWidth: Int get() = resources.displayMetrics.widthPixels
inline val Context.screenHeight: Int get() = resources.displayMetrics.heightPixels
inline val Context.screenWidthDip: Int get() = (screenWidth.toFloat() / density).roundToInt()
inline val Context.screenHeightDip: Int get() = (screenHeight.toFloat() / density).roundToInt()
