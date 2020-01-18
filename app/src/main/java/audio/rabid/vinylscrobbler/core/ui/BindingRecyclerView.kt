package audio.rabid.vinylscrobbler.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*


@Suppress("UNCHECKED_CAST")
abstract class BindingRecyclerView<T, V : View> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    interface OnItemClickListener<T, V : View> {
        fun onItemClick(view: V, item: T, adapter: RecyclerView.Adapter<SimpleViewHolder<V>>)
    }

    private var onItemClickListener: OnItemClickListener<T, V>? = null

    private inner class DiffUtilCallback(
        val old: List<T>,
        val new: List<T>
    ) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            isSameItem(old[oldItemPosition], new[newItemPosition])

        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            isSameContent(old[oldItemPosition], new[newItemPosition])
    }

    class SimpleViewHolder<V: View>(view: V) : RecyclerView.ViewHolder(view)

    private inner class Adapter(initialItems: List<T>) : RecyclerView.Adapter<SimpleViewHolder<V>>() {
        private var currentItems = initialItems

        fun setItems(items: List<T>) {
            val results = DiffUtil.calculateDiff(DiffUtilCallback(currentItems, items))
            currentItems = items
            results.dispatchUpdatesTo(this)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder<V> {
            return SimpleViewHolder(createView(parent, viewType))
        }

        fun getItem(position: Int): T = currentItems[position]

        override fun getItemCount(): Int = currentItems.size

        override fun onBindViewHolder(holder: SimpleViewHolder<V>, position: Int) {
            val item =  getItem(position)
            val view = holder.itemView as V
            bind(item, view).also {
                holder.itemView.setOnClickListener {
                    onItemClickListener?.onItemClick(view, item, this)
                }
            }
        }
    }

    fun setItems(items: List<T>) {
        ((adapter as? BindingRecyclerView<T,V>.Adapter)
            ?: Adapter(items).also { adapter = it }).setItems(items)
    }

    fun setOnItemClickListener(listener: OnItemClickListener<T, V>) {
        this.onItemClickListener = listener
    }

    abstract fun createView(parent: ViewGroup, viewType: Int): V

    abstract fun bind(item: T, view: V)

    abstract fun isSameItem(a: T, b: T): Boolean

    open fun isSameContent(a: T, b: T): Boolean = a == b
}

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
        // the triggered flag is a simple debounce so we only call it once until we leave and re-trigger the threshold
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
