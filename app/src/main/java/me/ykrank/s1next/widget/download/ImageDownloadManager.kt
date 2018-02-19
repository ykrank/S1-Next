package me.ykrank.s1next.widget.download

import android.app.Application
import cn.dreamtobe.filedownloader.OkHttp3Connection
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.database.NoDatabaseImpl
import com.liulishuo.filedownloader.util.FileDownloadUtils
import okhttp3.OkHttpClient
import java.io.File

/**
 * Created by ykrank on 2017/11/14.
 */
class ImageDownloadManager(private val okhttpBuilder: OkHttpClient.Builder) {
    private lateinit var dir: String

    fun setup(application: Application) {
        FileDownloader.setupOnApplicationOnCreate(application)
                .database(NoDatabaseImpl.createMaker())
                .connectionCreator(OkHttp3Connection.Creator(okhttpBuilder))

        FileDownloader.setGlobalPost2UIInterval(16)

        dir = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "image"
    }

    fun download(url: String, fileDownloadListener: FileDownloadListener): Int {
        return FileDownloader.getImpl().create(url)
                .setPath(dir, true)
                .setListener(fileDownloadListener)
                .start()
    }

    fun pause(downloadId: Int) {
        FileDownloader.getImpl().pause(downloadId)
    }

    fun clear(downloadId: Int, targetFilePath: String) {
        FileDownloader.getImpl().clear(downloadId, targetFilePath)
    }
}