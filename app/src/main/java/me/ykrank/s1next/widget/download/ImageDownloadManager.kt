package me.ykrank.s1next.widget.download

import android.app.Application
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher
import me.ykrank.s1next.data.api.ApiUtil
import okhttp3.OkHttpClient
import java.io.File

/**
 * Created by ykrank on 2017/11/14.
 */
class ImageDownloadManager(private val okhttpBuilder: OkHttpClient.Builder) {
    private lateinit var dir: File

    fun setup(application: Application) {
        val builder = OkDownload.Builder(application)
                .connectionFactory(DownloadOkHttp3Connection.Factory()
                        .setBuilder(okhttpBuilder))
                .build()
        OkDownload.setSingletonInstance(builder)

        DownloadDispatcher.setMaxParallelRunningCount(5)
        dir = File(application.cacheDir, "image/")
    }

    fun download(url: String, downloadListener: ImageDownloadListener): ImageDownloadTask {
        val task = DownloadTask.Builder(url, dir)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(300)
                // ignore the same task has already completed in the past.
                .setPassIfAlreadyCompleted(true)
                .setFilenameFromResponse(false)
                .setFilename(ApiUtil.getUrlId(url))
                .build()
        val imageDownloadTask = ImageDownloadTask(task)
        imageDownloadTask.enqueue(downloadListener)
        return imageDownloadTask
    }

    fun clear(downloadId: Int) {
        OkDownload.with().breakpointStore().remove(downloadId)
    }
}