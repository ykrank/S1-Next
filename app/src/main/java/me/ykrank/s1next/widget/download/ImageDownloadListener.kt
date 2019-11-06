package me.ykrank.s1next.widget.download

import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
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

