package audio.rabid.vinylscrobbler.core.ui

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KProperty

class ActivityViewBindingDelegate<VB : ViewBinding>
    internal constructor(private val block: (LayoutInflater) -> VB) : DefaultLifecycleObserver {
    private var binding: VB? = null

    // by supporting both getValue and onCreate lifecycle event, we can ensure that the content
    // view gets set.
    // if referenced in onCreate, they will be lazily created as soon as referenced
    // if not referenced in onCreate, they will be created at the end of onCreate
    operator fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): VB = loadBinding(thisRef)

    private fun loadBinding(owner: LifecycleOwner) : VB {
        return binding ?: synchronized(this) {
            binding ?: run {
                when (owner) {
                    is FragmentActivity ->
                        block.invoke(owner.layoutInflater).also { owner.setContentView(it.root) }
                    // Note: for fragments, you will need to implement onCreateView and return binding.root
                    is Fragment -> block.invoke(owner.layoutInflater)
                    else -> throw IllegalStateException("Unsupported lifecycle owner: $owner")
                }.also { binding = it }
            }
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        loadBinding(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        binding = null
        super.onDestroy(owner)
    }
}

fun <VB : ViewBinding> FragmentActivity.viewBinding(block: (LayoutInflater) -> VB) =
    ActivityViewBindingDelegate(block).also { lifecycle.addObserver(it) }

fun <VB : ViewBinding> Fragment.viewBinding(block: (LayoutInflater) -> VB) =
    ActivityViewBindingDelegate(block).also { lifecycle.addObserver(it) }
