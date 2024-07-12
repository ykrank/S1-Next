package me.ykrank.s1next.widget.download

import java.util.LinkedList

object ProgressManager {
    private val mListeners = HashMap<String, MutableList<ProgressListener>>()

    @Synchronized
    fun addListener(url: String, listener: ProgressListener) {
        var progressListeners = mListeners[url]
        if (progressListeners == null) {
            progressListeners = LinkedList()
            mListeners[url] = progressListeners
        }

        progressListeners.add(listener)
    }

    @Synchronized
    fun removeListener(url: String, listener: ProgressListener) {
        mListeners[url]?.remove(listener)
    }

    @Synchronized
    fun notifyProgress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
        val progressListeners = mListeners[task.url]
        if (progressListeners != null) {
            for (listener in progressListeners) {
                listener.onProgress(task, currentOffset, totalLength)
            }
        }
    }

    @Synchronized
    fun notifyTaskEnd(task: DownloadTask, cause: EndCause, realCause: java.lang.Exception?, model: ListenerModel) {
        val progressListeners = mListeners[task.url]
        if (progressListeners != null) {
            for (listener in progressListeners) {
                listener.taskEnd(task, cause, realCause, model)
            }
        }
    }
}

interface ProgressListener {
    fun onProgress(task: DownloadTask, currentOffset: Long, totalLength: Long)

    fun taskEnd(task: DownloadTask, cause: EndCause, realCause: java.lang.Exception?, model: ListenerModel)
}