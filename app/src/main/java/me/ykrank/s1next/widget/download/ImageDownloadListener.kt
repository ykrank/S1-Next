package me.ykrank.s1next.widget.download

import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener


/**
 * Created by ykrank on 2017/11/14.
 */
open class ImageDownloadListener() : FileDownloadListener() {

    override fun warn(task: BaseDownloadTask?) {

    }

    override fun completed(task: BaseDownloadTask?) {

    }

    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

    }

    override fun error(task: BaseDownloadTask?, e: Throwable?) {

    }

    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

    }

    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

    }

}