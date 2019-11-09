@file:Suppress("NOTHING_TO_INLINE")
package audio.rabid.vinylscrobbler

import com.squareup.contour.ContourLayout
import com.squareup.contour.XInt
import com.squareup.contour.YInt
import com.squareup.contour.solvers.XAxisSolver
import com.squareup.contour.solvers.YAxisSolver

inline fun ContourLayout.matchParentX(): XAxisSolver {
    return leftTo { parent.left() }.rightTo { parent.right() }
}

inline fun ContourLayout.matchParentY(): YAxisSolver {
    return topTo { parent.top() }.bottomTo { parent.bottom() }
}

private class PaddingXAxisSolver(val wrapped: XAxisSolver, val padding: XInt): XAxisSolver by wrapped {
    override fun max(): Int {
        return wrapped.max() - padding.value
    }

    override fun min(): Int {
        return wrapped.min() + padding.value
    }
}

private class PaddingYAxisSolver(val wrapped: YAxisSolver, val padding: YInt): YAxisSolver by wrapped {
    override fun max(): Int {
        return wrapped.max() - padding.value
    }

    override fun min(): Int {
        return wrapped.min() + padding.value
    }
}

fun XAxisSolver.withPadding(padding: XInt): XAxisSolver {
    return PaddingXAxisSolver(this, padding)
}

fun YAxisSolver.withPadding(padding: YInt): YAxisSolver {
    return PaddingYAxisSolver(this, padding)
}


