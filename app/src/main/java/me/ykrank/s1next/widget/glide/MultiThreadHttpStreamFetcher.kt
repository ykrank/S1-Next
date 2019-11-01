package me.ykrank.s1next.widget.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.AppException
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import me.ykrank.s1next.App
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.widget.download.ImageDownloadListener
import me.ykrank.s1next.widget.download.ImageDownloadManager
import okhttp3.Call
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

open class MultiThreadHttpStreamFetcher(client: Call.Factory, val url: GlideUrl) : OkHttpStreamFetcher(client, url) {

    @Inject
    lateinit var imageDownloadManager: ImageDownloadManager
    @Inject
    lateinit var mDownloadPreferencesManager: DownloadPreferencesManager

    var downloadTask: DownloadTask? = null
    private var stream: InputStream? = null
    var connecting = false
    var end = false

    init {
        App.appComponent.inject(this)
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        if (!mDownloadPreferencesManager.multiThreadDownload) {
            return super.loadData(priority, callback)
        }
        connecting = false
        end = false
        downloadTask = imageDownloadManager.download(url.toStringUrl(), object : ImageDownloadListener() {

            override fun taskStart(task: DownloadTask) {
                connecting = true
                L.d("多线程下载：${task.url}")
            }

            override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: java.lang.Exception?, model: Listener1Assist.Listener1Model) {
                end = true

                if (cause == EndCause.COMPLETED) {
                    L.d("多线程下载完成：${task.url}")
                    try {
                        stream = task.file?.inputStream()
                    } catch (e: IOException) {

                    }
                    callback.onDataReady(stream)
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
        try {
            stream?.close()
        } catch (e: IOException) {
            // Ignored
        }

        downloadTask = null
    }

    override fun cancel() {
        if (!connecting) {
            downloadTask?.cancel()
        }
    }

    override fun getDataSource(): DataSource {
        return if (mDownloadPreferencesManager.multiThreadDownload) DataSource.LOCAL else super.getDataSource()
    }
}
