package me.ykrank.s1next.widget.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.AppException
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.StatusUtil
import com.liulishuo.okdownload.core.cause.EndCause
import me.ykrank.s1next.App
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.widget.download.ImageDownloadListener
import me.ykrank.s1next.widget.download.ImageDownloadManager
import okhttp3.Call
import java.io.InputStream
import javax.inject.Inject

open class MultiThreadHttpStreamFetcher(client: Call.Factory, val url: GlideUrl) : OkHttpStreamFetcher(client, url) {

    @Inject
    lateinit var imageDownloadManager: ImageDownloadManager
    @Inject
    lateinit var mDownloadPreferencesManager: DownloadPreferencesManager

    var downloadTask: DownloadTask? = null

    init {
        App.appComponent.inject(this)
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        if (!mDownloadPreferencesManager.multiThreadDownload) {
            return super.loadData(priority, callback)
        }
        downloadTask = imageDownloadManager.download(url.toStringUrl(), object : ImageDownloadListener() {

            override fun taskStart(task: DownloadTask) {
                L.d("多线程下载：${task.url}")
            }

            override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) {
                if (cause == EndCause.COMPLETED || StatusUtil.isCompleted(task)) {
                    L.d("多线程下载完成：${task.url}")
                    callback.onDataReady(task.file?.inputStream())
                } else {
                    L.d("多线程下载失败：${task.url}")
                    if (realCause != null) {
                        L.d(realCause)
                    }
                    callback.onLoadFailed(realCause ?: AppException("Image download error"))
                }
            }
        })
    }

    override fun cleanup() {
        downloadTask = null
    }

    override fun cancel() {
        downloadTask?.cancel()
    }

    override fun getDataSource(): DataSource {
        return if (mDownloadPreferencesManager.multiThreadDownload) DataSource.LOCAL else super.getDataSource()
    }
}
