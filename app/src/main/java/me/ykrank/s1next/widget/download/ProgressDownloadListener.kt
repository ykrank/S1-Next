package me.ykrank.s1next.widget.download

import androidx.annotation.CallSuper
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import java.lang.Exception

open class ProgressDownloadListener : ImageDownloadListener(){

    @CallSuper
    override fun progress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
        super.progress(task, currentOffset, totalLength)
        ProgressManager.notifyProgress(task, currentOffset, totalLength)
    }

    @CallSuper
    override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?, model: Listener1Assist.Listener1Model) {
        super.taskEnd(task, cause, realCause, model)
        ProgressManager.notifyTaskEnd(task, cause, realCause, model)
    }
}