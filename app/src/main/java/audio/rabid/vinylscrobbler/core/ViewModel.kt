package audio.rabid.vinylscrobbler.core

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.Closeable

abstract class ViewModel : LifecycleObserver {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val processTracker = ProcessTracker()

    fun launch(block: suspend () -> Unit): Job {
        return coroutineScope.launch { block.invoke() }.also { processTracker.addJob(it) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun close() {
        processTracker.close()
    }
}

class ProcessTracker : Closeable {

    private val jobs = mutableListOf<Job>()

    fun addJob(job: Job) {
        synchronized(this) {
            jobs.add(job)
        }
    }

    override fun close() {
        synchronized(this) {
            for (job in jobs) {
                job.cancel()
            }
            jobs.clear()
        }
    }
}
