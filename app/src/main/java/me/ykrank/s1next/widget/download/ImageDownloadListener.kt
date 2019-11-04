package me.ykrank.s1next.widget.download

import com.github.ykrank.androidtools.util.L
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.StatusUtil
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist


/**
 * Created by ykrank on 2017/11/14.
 */
open class ImageDownloadListener {

    open fun taskStart(task: DownloadTask) {

    }

    /**
     * If completed conflict task, model may be wrong
     */
    open fun taskEnd(task: DownloadTask, cause: EndCause, realCause: java.lang.Exception?, model: Listener1Assist.Listener1Model) {

    }

    open fun progress(task: DownloadTask, currentOffset: Long, totalLength: Long) {

    }

    open fun connected(task: DownloadTask, blockCount: Int, currentOffset: Long, totalLength: Long) {

    }

}

/**
 * 内部还维护一个和当前任务冲突的任务，完成时通知它们
 */
class AppDownloadListener : DownloadListener1 {

    private val imageDownloadListener: ImageDownloadListener?
    private val wrapDownloadListener: DownloadListener?

    private val conflictListeners: MutableList<DownloadListener1> = mutableListOf()

    constructor(imageDownloadListener: ImageDownloadListener?) : super() {
        this.imageDownloadListener = imageDownloadListener
        this.wrapDownloadListener = null
    }

    private constructor(wrapDownloadListener: DownloadListener?) {
        this.imageDownloadListener = null
        this.wrapDownloadListener = wrapDownloadListener
    }

    override fun taskStart(task: DownloadTask, model: Listener1Assist.Listener1Model) {
        wrapDownloadListener?.taskStart(task)

        imageDownloadListener?.taskStart(task)
        conflictListeners.forEach {
            it.taskStart(task, model)
        }
    }

    override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: java.lang.Exception?, model: Listener1Assist.Listener1Model) {
        wrapDownloadListener?.taskEnd(task, cause, realCause)

        if (cause == EndCause.FILE_BUSY || cause == EndCause.SAME_TASK_BUSY) {
            val busyTask = OkDownload.with().downloadDispatcher().findSameTask(task)
            if (busyTask != null) {
                synchronized(busyTask) {
                    var busyListener = busyTask.listener
                    if (busyListener !is AppDownloadListener) {
                        busyListener = AppDownloadListener(busyListener)
                    }
                    busyListener.addConflictListener(this)
                    busyTask.replaceListener(busyListener)
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
        L.d("progress ${task.url} $currentOffset / $totalLength")
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

    fun addConflictListener(listener: DownloadListener1?) {
        listener?.let {
            conflictListeners.add(it)
        }
    }

    //以下为调用包装的原Listener
    override fun connectTrialStart(task: DownloadTask, requestHeaderFields: MutableMap<String, MutableList<String>>) {
        super.connectTrialStart(task, requestHeaderFields)
        wrapDownloadListener?.connectTrialStart(task, requestHeaderFields)
    }

    override fun connectTrialEnd(task: DownloadTask, responseCode: Int, responseHeaderFields: MutableMap<String, MutableList<String>>) {
        super.connectTrialEnd(task, responseCode, responseHeaderFields)
        wrapDownloadListener?.connectTrialEnd(task, responseCode, responseHeaderFields)
    }

    override fun downloadFromBeginning(task: DownloadTask, info: BreakpointInfo, cause: ResumeFailedCause) {
        super.downloadFromBeginning(task, info, cause)
        wrapDownloadListener?.downloadFromBeginning(task, info, cause)
    }

    override fun downloadFromBreakpoint(task: DownloadTask, info: BreakpointInfo) {
        super.downloadFromBreakpoint(task, info)
        wrapDownloadListener?.downloadFromBreakpoint(task, info)
    }

    override fun connectStart(task: DownloadTask, blockIndex: Int, requestHeaderFields: MutableMap<String, MutableList<String>>) {
        super.connectStart(task, blockIndex, requestHeaderFields)
        wrapDownloadListener?.connectStart(task, blockIndex, requestHeaderFields)
    }

    override fun connectEnd(task: DownloadTask, blockIndex: Int, responseCode: Int, responseHeaderFields: MutableMap<String, MutableList<String>>) {
        super.connectEnd(task, blockIndex, responseCode, responseHeaderFields)
        wrapDownloadListener?.connectEnd(task, blockIndex, responseCode, responseHeaderFields)
    }

    override fun fetchStart(task: DownloadTask, blockIndex: Int, contentLength: Long) {
        super.fetchStart(task, blockIndex, contentLength)
        wrapDownloadListener?.fetchStart(task, blockIndex, contentLength)
    }

    override fun fetchProgress(task: DownloadTask, blockIndex: Int, increaseBytes: Long) {
        super.fetchProgress(task, blockIndex, increaseBytes)
        wrapDownloadListener?.fetchProgress(task, blockIndex, increaseBytes)
    }

    override fun fetchEnd(task: DownloadTask, blockIndex: Int, contentLength: Long) {
        super.fetchEnd(task, blockIndex, contentLength)
        wrapDownloadListener?.fetchEnd(task, blockIndex, contentLength)
    }

}