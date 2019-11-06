package me.ykrank.s1next.widget.download

import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.StatusUtil
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist

/**
 * 内部还维护一个和当前任务冲突的任务，完成时通知它们
 */
class AppDownloadListener(private var imageDownloadListener: ImageDownloadListener?) : DownloadListener1() {
    private val conflictListeners: MutableList<DownloadListener1> = mutableListOf()
    private var end = false

    override fun taskStart(task: DownloadTask, model: Listener1Assist.Listener1Model) {
        imageDownloadListener?.taskStart(task)
        conflictListeners.forEach {
            it.taskStart(task, model)
        }
    }

    override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: java.lang.Exception?, model: Listener1Assist.Listener1Model) {
        end = true
        if (cause == EndCause.FILE_BUSY || cause == EndCause.SAME_TASK_BUSY) {
            val busyTask = OkDownload.with().downloadDispatcher().findSameTask(task)
            if (busyTask != null) {
                synchronized(busyTask) {
                    val busyListener = busyTask.listener as AppDownloadListener?
                    if (busyListener == null) {
                        busyTask.replaceListener(this)
                    } else {
                        busyListener.addConflictListener(this)
                    }
                }
            } else {
                // Busy but could not find busy task, may be completed
                if (StatusUtil.isCompleted(task)) {
                    realEnd(task, EndCause.COMPLETED, null, model)
                } else {
                    realEnd(task, cause, realCause, model)
                }
            }
            return
        }
        realEnd(task, cause, realCause, model)
    }

    private fun realEnd(task: DownloadTask, cause: EndCause, realCause: java.lang.Exception?, model: Listener1Assist.Listener1Model) {
        imageDownloadListener?.taskEnd(task, cause, realCause, model)

        conflictListeners.forEach {
            it.taskEnd(task, cause, realCause, model)
        }
    }

    override fun progress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
//        L.d("progress ${task.url} $currentOffset / $totalLength")
        imageDownloadListener?.progress(task, currentOffset, totalLength)

        conflictListeners.forEach {
            it.progress(task, currentOffset, totalLength)
        }
    }

    override fun connected(task: DownloadTask, blockCount: Int, currentOffset: Long, totalLength: Long) {
        imageDownloadListener?.connected(task, blockCount, currentOffset, totalLength)

        conflictListeners.forEach {
            it.connected(task, blockCount, currentOffset, totalLength)
        }
    }

    override fun retry(task: DownloadTask, cause: ResumeFailedCause) {
        conflictListeners.forEach {
            it.retry(task, cause)
        }
    }

    @Synchronized
    fun addConflictListener(listener: DownloadListener1?) {
        listener?.let {
            conflictListeners.add(it)
        }
    }

    @Synchronized
    fun clear() {
        imageDownloadListener = null
    }

    @Synchronized
    fun couldCancel(): Boolean {
        return !end && conflictListeners.isEmpty()
    }
}