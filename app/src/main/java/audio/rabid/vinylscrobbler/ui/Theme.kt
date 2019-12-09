package audio.rabid.vinylscrobbler.ui

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt
import audio.rabid.vinylscrobbler.core.ui.density
import group.infotech.drawable.dsl.layerDrawable
import group.infotech.drawable.dsl.shapeDrawable
import group.infotech.drawable.dsl.solidColor

object Theme {
    object Colors {
        // TODO: these are the generic material design colors, pick some better ones

        @ColorInt
        private val lightOnColor = Color.WHITE

        @ColorInt
        private val darkOnColor = Color.BLACK

        val primary = ThemeColor("#6200EE", lightOnColor)
        val primaryDark = ThemeColor("#3700B3", lightOnColor)
        val primaryLight = ThemeColor("#BB86FC", darkOnColor)
        val accent = ThemeColor("#03DAC6", darkOnColor)
        val accentDark = ThemeColor("#018786", lightOnColor)
        val background = ThemeColor("#FFFFFF", darkOnColor)
        val surface = ThemeColor("#FFFFFF", darkOnColor)
        val error = ThemeColor("#B00020", lightOnColor)

        class ThemeColor(colorHex: String, @ColorInt val onColor: Int) {

// neither luminance > 50% or greater contrast map to material design guidelines...
//        @ColorInt
//        fun @receiver:ColorInt Int.visibleColor(
//            @ColorInt light: Int = Color.WHITE,
//            @ColorInt dark: Int = Color.BLACK
//        ): Int {
//            // if (this.isDark) light else dark
//            return arrayOf(light, dark).maxBy { ColorUtils.calculateContrast(this, it) }!!
//        }
//        val @receiver:ColorInt Int.isDark get() = ColorUtils.calculateLuminance(this) < 0.5
            @ColorInt
            val color: Int = Color.parseColor(colorHex)
        }
    }

    object Dimensions {
        val standardMargin = 8

        val cornerRadius = 3

        val elevation = 8
    }
}

fun View.cardTheme() {
    elevation = Theme.Dimensions.elevation * density
    background = shapeDrawable {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = Theme.Dimensions.cornerRadius * density
        solidColor = Theme.Colors.background.color
    }
    clipToOutline = true
}

//inline val ContourLayout.standardMargin get() = Theme.Dimensions.standardMarginDp.dip
