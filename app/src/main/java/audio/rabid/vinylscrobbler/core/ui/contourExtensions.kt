@file:Suppress("NOTHING_TO_INLINE")
package audio.rabid.vinylscrobbler.core.ui

import com.squareup.contour.XInt
import com.squareup.contour.YInt
import com.squareup.contour.solvers.XAxisSolver
import com.squareup.contour.solvers.YAxisSolver

private class MarginXAxisSolver(val wrapped: XAxisSolver, val padding: XInt): XAxisSolver by wrapped {
    override fun max(): Int {
        return wrapped.max() - padding.value
    }

    override fun min(): Int {
        return wrapped.min() + padding.value
    }
}

private class MarginYAxisSolver(val wrapped: YAxisSolver, val padding: YInt): YAxisSolver by wrapped {
    override fun max(): Int {
        return wrapped.max() - padding.value
    }

    override fun min(): Int {
        return wrapped.min() + padding.value
    }
}

fun XAxisSolver.withMargin(margin: XInt): XAxisSolver = MarginXAxisSolver(this, margin)
fun YAxisSolver.withMargin(margin: YInt): YAxisSolver = MarginYAxisSolver(this, margin)
