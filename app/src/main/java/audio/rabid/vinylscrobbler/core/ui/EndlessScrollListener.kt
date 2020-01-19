package audio.rabid.vinylscrobbler.core.ui

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class EndlessScrollListener(
    private val layoutManager: RecyclerView.LayoutManager,
    rowThreshold: Int,
    private val callback: () -> Unit
) : RecyclerView.OnScrollListener() {

    private val visibleThreshold = when (layoutManager) {
        is GridLayoutManager -> rowThreshold * layoutManager.spanCount
        is StaggeredGridLayoutManager -> rowThreshold * layoutManager.spanCount
        else -> rowThreshold
    }

    var enabled = true
    private var triggered = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!enabled || dy <= 0) return
        val shouldLoadMore = lastVisibleItemPosition + visibleThreshold > layoutManager.itemCount
        // the triggered flag is a simple debounce so we only call it once until we leave and
        // re-trigger the threshold
        if (shouldLoadMore && !triggered) {
            callback.invoke()
            triggered = true
        } else if (!shouldLoadMore && triggered) {
            triggered = false
        }
    }

    private val lastVisibleItemPosition: Int
        get() = when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
            is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
            is StaggeredGridLayoutManager ->
                layoutManager.findLastCompletelyVisibleItemPositions(null).max() ?: 0
            else -> throw IllegalArgumentException("This LayoutManager is not supported")
        }
}

fun RecyclerView.setOnEndlessScrollLoadMoreCallback(rowThreshold: Int = 5, callback: () -> Unit) {
    val layoutManager = layoutManager
        ?: throw IllegalStateException("RecyclerView must have a layout manager")
    addOnScrollListener(EndlessScrollListener(layoutManager, rowThreshold, callback))
}
