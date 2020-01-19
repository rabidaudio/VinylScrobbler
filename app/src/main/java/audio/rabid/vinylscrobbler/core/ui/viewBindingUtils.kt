package audio.rabid.vinylscrobbler.core.ui

import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KProperty

class ActivityViewBindingDelegate<VB : ViewBinding>(private val block: (LayoutInflater) -> VB) {
    private var bound = false
    private lateinit var binding: VB

    operator fun getValue(thisRef: FragmentActivity, property: KProperty<*>): VB {
        if (!bound) {
            binding = block.invoke(thisRef.layoutInflater).also { binding ->
                thisRef.setContentView(binding.root)
            }
            bound = true
        }
        return binding
    }
}

fun <VB : ViewBinding> FragmentActivity.viewBinding(block: (LayoutInflater) -> VB) =
    ActivityViewBindingDelegate(block)
