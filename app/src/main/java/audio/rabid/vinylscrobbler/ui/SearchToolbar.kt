package audio.rabid.vinylscrobbler.ui

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import audio.rabid.vinylscrobbler.databinding.SearchToolbarBinding

class SearchToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = SearchToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.BackArrow.setOnClickListener {
            (context as Activity).finish()
        }
    }

    fun setQuery(query: String) {
        binding.SearchBox.setText(query)
    }

    fun setQueryFromIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEARCH) {
            binding.SearchBox.setText(intent.getStringExtra(SearchManager.QUERY))
        }
    }

    fun setOnSearchListener(afterTextChanged: (Editable?) -> Unit) {
        binding.SearchBox.addTextChangedListener(afterTextChanged = afterTextChanged)
    }
}
