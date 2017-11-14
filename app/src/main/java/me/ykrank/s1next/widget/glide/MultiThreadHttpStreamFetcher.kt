package me.ykrank.s1next.widget.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.github.ykrank.androidtools.widget.AppException
import com.liulishuo.filedownloader.BaseDownloadTask
import me.ykrank.s1next.App
import me.ykrank.s1next.widget.download.ImageDownloadListener
import me.ykrank.s1next.widget.download.ImageDownloadManager
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception
import javax.inject.Inject

open class MultiThreadHttpStreamFetcher(val url: GlideUrl) : DataFetcher<InputStream> {
    @Inject
    internal lateinit var imageDownloadManager: ImageDownloadManager
    var downloadId: Int = 0
    var targetFilePath: String? = null

    init {
        App.appComponent.inject(this)
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        downloadId = imageDownloadManager.download(url.toStringUrl(), object : ImageDownloadListener() {
            override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                super.pending(task, soFarBytes, totalBytes)
                targetFilePath = task?.targetFilePath
            }

            override fun completed(task: BaseDownloadTask?) {
                super.completed(task)
                callback.onDataReady(FileInputStream(task?.targetFilePath))
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                super.error(task, e)
                if (e is Exception) {
                    callback.onLoadFailed(e)
                } else {
                    callback.onLoadFailed(AppException(e))
                }
            }
        })
    }

    override fun cleanup() {
        targetFilePath?.let { imageDownloadManager.clear(downloadId, it) }
    }

    override fun cancel() {
        imageDownloadManager.pause(downloadId)
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }
}
