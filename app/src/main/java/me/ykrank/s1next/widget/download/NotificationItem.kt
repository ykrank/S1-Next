package me.ykrank.s1next.widget.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.liulishuo.filedownloader.model.FileDownloadStatus
import com.liulishuo.filedownloader.notification.BaseNotificationItem
import com.liulishuo.filedownloader.util.FileDownloadHelper
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.view.activity.ForumActivity


/**
 * Created by ykrank on 2017/11/14.
 */
class NotificationItem constructor(id: Int, title: String, desc: String) : BaseNotificationItem(id, title, desc) {

    internal var pendingIntent: PendingIntent
    internal var builder: NotificationCompat.Builder

    init {
        val intent = Intent.makeMainActivity(ComponentName(App.get(),
                ForumActivity::class.java))
        this.pendingIntent = PendingIntent.getActivity(App.get(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(ChannelID, "图片下载", NotificationManager.IMPORTANCE_LOW)
            mChannel.description = desc
            mChannel.lightColor = Color.CYAN
            mChannel.canShowBadge()
            mChannel.setShowBadge(true)

            val nm = App.get().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
            nm.createNotificationChannel(mChannel)
        }

        builder = NotificationCompat.Builder(FileDownloadHelper.getAppContext(), ChannelID)

        builder.setDefaults(Notification.DEFAULT_LIGHTS)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle(getTitle())
                .setContentText(desc)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)

    }

    override fun show(statusChanged: Boolean, status: Int, isShowProgress: Boolean) {

        var desc = desc
        when (status.toByte()) {
            FileDownloadStatus.pending -> desc += " pending"
            FileDownloadStatus.started -> desc += " started"
            FileDownloadStatus.progress -> desc += " progress"
            FileDownloadStatus.retry -> desc += " retry"
            FileDownloadStatus.error -> desc += " error"
            FileDownloadStatus.paused -> desc += " paused"
            FileDownloadStatus.completed -> desc += " completed"
            FileDownloadStatus.warn -> desc += " warn"
        }

        builder.setContentTitle(title)
                .setContentText(desc)


        if (statusChanged) {
            builder.setTicker(desc)
        }

        builder.setProgress(total, sofar, !isShowProgress)
        manager.notify(id, builder.build())
    }

    companion object {
        val ChannelID = "ImageDownload"
    }
}