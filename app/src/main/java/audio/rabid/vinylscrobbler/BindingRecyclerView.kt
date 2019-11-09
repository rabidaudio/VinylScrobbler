package audio.rabid.vinylscrobbler

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.contour.XInt
import com.squareup.contour.YInt


@Suppress("UNCHECKED_CAST")
abstract class BindingRecyclerView<T, V : View> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

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

    private class SimpleViewHolder<V: View>(view: V) : RecyclerView.ViewHolder(view)

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
            bind(getItem(position), holder.itemView as V)
        }
    }

    fun setItems(items: List<T>) {
        ((adapter as? BindingRecyclerView<T,V>.Adapter)
            ?: Adapter(items).also { adapter = it }).setItems(items)
    }

    abstract fun createView(parent: ViewGroup, viewType: Int): V

    abstract fun bind(item: T, view: V)

    abstract fun isSameItem(a: T, b: T): Boolean

    open fun isSameContent(a: T, b: T): Boolean = a == b

    val Int.dip: Int get() = (density * this).toInt()
    val Int.xdip: XInt get() = XInt((density * this).toInt())
    val Int.ydip: YInt get() = YInt((density * this).toInt())
}
