package audio.rabid.vinylscrobbler.ui

import android.app.Activity
import android.content.Context
import audio.rabid.vinylscrobbler.R

class ActivityTheme(context: Context) : Theme,
    Theme.Colors,
    Theme.Dimensions {

    override val colors: Theme.Colors get() = this
    override val dimensions: Theme.Dimensions get() = this

    override val primary: Theme.ThemeColor =
        Theme.ThemeColor(
            context.getColor(R.color.primary),
            context.getColor(R.color.onPrimary)
        )

    override val primaryDark: Theme.ThemeColor =
        Theme.ThemeColor(
            context.getColor(R.color.primary),
            context.getColor(R.color.onPrimary)
        )

    override val primaryLight: Theme.ThemeColor =
        Theme.ThemeColor(
            context.getColor(R.color.primaryDark),
            context.getColor(R.color.onPrimaryDark)
        )

    override val accent: Theme.ThemeColor =
        Theme.ThemeColor(
            context.getColor(R.color.accent),
            context.getColor(R.color.onAccent)
        )

    override val accentDark: Theme.ThemeColor =
        Theme.ThemeColor(
            context.getColor(R.color.accentDark),
            context.getColor(R.color.onAccentDark)
        )

    override val surface: Theme.ThemeColor =
        Theme.ThemeColor(
            context.getColor(R.color.surface),
            context.getColor(R.color.onSurface)
        )

    override val background: Theme.ThemeColor =
        Theme.ThemeColor(
            context.getColor(R.color.background),
            context.getColor(R.color.onBackground)
        )

    override val error: Theme.ThemeColor =
        Theme.ThemeColor(
            context.getColor(R.color.error),
            context.getColor(R.color.onError)
        )

    override val standardMargin = context.resources.getDimensionPixelSize(R.dimen.standardMargin)

    override val fabMargin: Int = context.resources.getDimensionPixelSize(R.dimen.fabMargin)
}
