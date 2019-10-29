package me.ykrank.s1next.widget.download

import android.app.Application
import android.net.Uri
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher
import me.ykrank.s1next.util.md5
import okhttp3.OkHttpClient
import java.io.File
import java.security.MessageDigest

/**
 * Created by ykrank on 2017/11/14.
 */
class ImageDownloadManager(private val okhttpBuilder: OkHttpClient.Builder) {
    private lateinit var dir: File

    private val digest = MessageDigest.getInstance("MD5")

    fun setup(application: Application) {
        val builder = OkDownload.Builder(application)
                .connectionFactory(DownloadOkHttp3Connection.Factory()
                        .setBuilder(okhttpBuilder))
                .build()
        OkDownload.setSingletonInstance(builder)

        DownloadDispatcher.setMaxParallelRunningCount(5)
        dir = File(application.cacheDir, "image/")
    }

    fun download(url: String, downloadListener: DownloadListener): DownloadTask {
        val task = DownloadTask.Builder(url, dir)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(3000)
                // ignore the same task has already completed in the past.
                .setPassIfAlreadyCompleted(true)
                .setFilenameFromResponse(false)
                .setFilename(digest.md5(url))
                .build()
        task.enqueue(downloadListener)
        return task
    }

    fun pause(task: DownloadTask) {
        task.cancel()
    }

    fun clear(downloadId: Int) {
        OkDownload.with().breakpointStore().remove(downloadId)
    }
}