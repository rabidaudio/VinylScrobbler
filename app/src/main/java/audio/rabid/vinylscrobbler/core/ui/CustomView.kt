@file:Suppress("NOTHING_TO_INLINE")
package audio.rabid.vinylscrobbler.core.ui

import android.content.Context
import android.view.View
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.updateLayoutParams
import audio.rabid.vinylscrobbler.ui.theme
import com.squareup.contour.*
import com.squareup.contour.solvers.XAxisSolver
import com.squareup.contour.solvers.YAxisSolver

abstract class CustomView(context: Context) : ContourLayout(context) {

    constructor(context: Context, @StyleRes styleId: Int) :
            this(ContextThemeWrapper(context, styleId))

    val theme by lazy { this.context.theme() }

    val standardMarginX: XInt get() = theme.dimensions.standardMargin.toXInt()
    val standardMarginY: YInt get() = theme.dimensions.standardMargin.toYInt()

    private var margins = arrayOf(0, 0, 0, 0)
    private var initialized = false

    private val onInitializeCallbacks = mutableSetOf<() -> Unit>()

    fun onLayoutInitialized(callback: () -> Unit) {
        check(!initialized) { "View has already initialized it's layout" }
        onInitializeCallbacks.add(callback)
    }

    override fun onInitializeLayout() {
        super.onInitializeLayout()
        initialized = true
        for (callback in onInitializeCallbacks) {
            callback.invoke()
        }
        onInitializeCallbacks.clear()
    }

    protected fun setMargin(margin: Int) {
        setMargin(margin.toXInt(), margin.toYInt(), margin.toXInt(), margin.toYInt())
    }

    protected fun setMargin(x: XInt, y: YInt) {
        setMargin(x, y, x, y)
    }

    protected fun setMargin(left: XInt, top: YInt, right: XInt, bottom: YInt) {
        margins = arrayOf(left.value, top.value, right.value, bottom.value)
        if (initialized) applyMargins() else onLayoutInitialized { applyMargins() }
    }

    private fun applyMargins() {
        updateLayoutParams<MarginLayoutParams> {
            val (left, top, right, bottom) = margins
            setMargins(left, top, right, bottom)
        }
    }

    inline fun below(other: View) = topTo { other.bottom() }
    inline fun above(other: View) = bottomTo { other.top() }
    inline fun rightOf(other: View) = leftTo { other.right() }
    inline fun leftOf(other: View) = rightTo { other.left() }
    inline fun HasBottom.below(other: View) = topTo { other.bottom() }
    inline fun HasTop.above(other: View) = bottomTo { other.top() }
    inline fun HasRight.rightOf(other: View) = leftTo { other.right() }
    inline fun HasLeft.leftOf(other: View) = rightTo { other.left() }

    inline fun toParentTop() = topTo { parent.top() }
    inline fun toParentBottom() = bottomTo { parent.bottom() }
    inline fun toParentLeft() = leftTo { parent.left() }
    inline fun toParentRight() = rightTo { parent.right() }
    inline fun HasBottom.toParentTop() = topTo { parent.top() }
    inline fun HasTop.toParentBottom() = bottomTo { parent.bottom() }
    inline fun HasRight.toParentLeft() = leftTo { parent.left() }
    inline fun HasLeft.toParentRight() = rightTo { parent.right() }

    inline fun matchParentX(): XAxisSolver = toParentLeft().toParentRight()
    inline fun matchParentY(): YAxisSolver = toParentTop().toParentBottom()

    inline fun XAxisSolver.withDefaultMargin() = withMargin(standardMarginX)
    inline fun YAxisSolver.withDefaultMargin() = withMargin(standardMarginY)

    inline fun XAxisSolver.withHalfMargin() = withMargin(standardMarginX / 2)
    inline fun YAxisSolver.withHalfMargin() = withMargin(standardMarginY / 2)
}
