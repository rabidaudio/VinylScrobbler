package audio.rabid.vinylscrobbler.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class ViewBindingAdapter<T, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB
) : RecyclerView.Adapter<ViewBindingAdapter.ViewHolder<VB>>() {

    data class ViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    private var currentItems: List<T> = emptyList()

    abstract fun isSameItem(a: T, b: T): Boolean

    open fun isSameContent(a: T, b: T): Boolean = a == b

    abstract fun bind(item: T, viewBinding: VB)

    fun setItems(items: List<T>) {
        val results = DiffUtil.calculateDiff(DiffUtilCallback(currentItems, items))
        currentItems = items
        results.dispatchUpdatesTo(this)
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VB> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = bindingInflater.invoke(inflater, parent, false)
        return ViewHolder(binding)
    }

    fun getItem(position: Int): T = currentItems[position]

    final override fun getItemCount(): Int = currentItems.size

    open fun onItemClick(item: T, viewBinding: VB) {
        // no-op
    }

    final override fun onBindViewHolder(holder: ViewHolder<VB>, position: Int) {
        val item = getItem(position)
        bind(item, holder.binding)
        holder.itemView.setOnClickListener {
            onItemClick(item, holder.binding)
        }
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
}
