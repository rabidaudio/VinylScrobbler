package audio.rabid.vinylscrobbler.ui

import android.content.Context
import androidx.annotation.ColorInt

interface Theme {

    class ThemeColor(@ColorInt val color: Int, @ColorInt val onColor: Int)

    val colors: Colors

    interface Colors {
        val primary: ThemeColor
        val primaryDark: ThemeColor
        val primaryLight: ThemeColor
        val accent: ThemeColor
        val accentDark: ThemeColor

        val background: ThemeColor
        val surface: ThemeColor
        val error: ThemeColor
    }

    val dimensions: Dimensions

    interface Dimensions {
        val standardMargin: Int
        val fabMargin: Int
    }
}

fun Context.theme() = ActivityTheme(this)
