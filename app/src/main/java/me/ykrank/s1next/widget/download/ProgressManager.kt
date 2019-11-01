package me.ykrank.s1next.widget.download

import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import java.util.*

object ProgressManager {
    private val mListeners = WeakHashMap<String, MutableList<ProgressListener>>()

    fun addListener(url: String, listener: ProgressListener) {
        var progressListeners: MutableList<ProgressListener>?
        synchronized(ProgressManager::class.java) {
            progressListeners = mListeners[url]
            if (progressListeners == null) {
                progressListeners = LinkedList()
                mListeners[url] = progressListeners
            }
        }
        progressListeners?.add(listener)
    }

    fun notifyProgress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
        val progressListeners = mListeners[task.url]
        if (progressListeners != null) {
            val array = progressListeners.toTypedArray()
            for (i in array.indices) {
                array[i].onProgress(task, currentOffset, totalLength)
            }
        }
    }

    fun notifyTaskEnd(task: DownloadTask, cause: EndCause, realCause: java.lang.Exception?, model: Listener1Assist.Listener1Model) {
        val progressListeners = mListeners[task.url]
        if (progressListeners != null) {
            val array = progressListeners.toTypedArray()
            for (i in array.indices) {
                array[i].taskEnd(task, cause, realCause, model)
            }
        }
    }
}

interface ProgressListener {
    fun onProgress(task: DownloadTask, currentOffset: Long, totalLength: Long)

    fun taskEnd(task: DownloadTask, cause: EndCause, realCause: java.lang.Exception?, model: Listener1Assist.Listener1Model)
}