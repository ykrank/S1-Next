package me.ykrank.s1next.widget.download

import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.OkDownload

class ImageDownloadTask(val task: DownloadTask) {

    fun enqueue(listener: ImageDownloadListener) {
        task.enqueue(AppDownloadListener(listener))
    }

    @Synchronized
    fun cancel() {
        val listener: AppDownloadListener? = task.listener as AppDownloadListener?
        if (listener != null) {
            listener.clear()

            if (listener.couldCancel() && OkDownload.with().downloadDispatcher().findSameTask(task) == task) {
                task.cancel()
            }
        }
    }
}