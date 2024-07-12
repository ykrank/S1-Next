package me.ykrank.s1next.widget.download

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class ProgressManager {
    private val mListeners = ConcurrentHashMap<String, CopyOnWriteArrayList<ProgressListener>>()

    fun addListener(url: String, listener: ProgressListener) {
        var progressListeners = mListeners[url]
        if (progressListeners == null) {
            synchronized(mListeners) {
                progressListeners = mListeners[url]
                if (progressListeners == null) {
                    val listeners = CopyOnWriteArrayList<ProgressListener>()
                    progressListeners = listeners
                    mListeners[url] = listeners
                }
            }
        }

        progressListeners?.add(listener)
    }

    fun removeListener(url: String, listener: ProgressListener) {
        mListeners[url]?.remove(listener)
    }

    fun notifyProgress(task: DownloadTask, progress: DownloadProgressModel) {
        val progressListeners = mListeners[task.url]
        progressListeners?.forEach {
            it.onProgress(task, progress)
        }
    }
}

interface ProgressListener {
    fun onProgress(task: DownloadTask, progress: DownloadProgressModel)
}