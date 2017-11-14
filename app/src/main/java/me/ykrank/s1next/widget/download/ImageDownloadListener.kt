package me.ykrank.s1next.widget.download

import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.BuildConfig
import com.liulishuo.filedownloader.notification.BaseNotificationItem
import com.liulishuo.filedownloader.notification.FileDownloadNotificationHelper
import com.liulishuo.filedownloader.notification.FileDownloadNotificationListener


/**
 * Created by ykrank on 2017/11/14.
 */
open class ImageDownloadListener() : FileDownloadNotificationListener(FileDownloadNotificationHelper<NotificationItem>()) {

    override fun create(task: BaseDownloadTask): BaseNotificationItem {
        return NotificationItem(task.id, task.url, task.url)
    }

    override fun connected(task: BaseDownloadTask, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
        super.connected(task, etag, isContinue, soFarBytes, totalBytes)
        helper.get(task.id).title = task.filename
    }

    override fun interceptCancel(task: BaseDownloadTask?, notificationItem: BaseNotificationItem?): Boolean {
        return BuildConfig.DEBUG
    }
}