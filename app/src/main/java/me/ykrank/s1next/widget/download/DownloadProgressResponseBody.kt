package me.ykrank.s1next.widget.download

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer
import java.io.IOException

/**
 * Created by ykrank on 7/12/24
 * 
 */
class DownloadProgressResponseBody(
    val progressManager: ProgressManager,
    val downloadTask: DownloadTask,
    val responseBody: ResponseBody,
) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null

    override fun contentLength(): Long = responseBody.contentLength()

    override fun contentType(): MediaType? = responseBody.contentType()

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = getforwardSource(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    private fun getforwardSource(source: Source): Source =
        object : ForwardingSource(source) {
            var totalBytesRead = 0L

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                progressManager.notifyProgress(
                    downloadTask,
                    DownloadProgressModel(
                        responseBody.contentLength(),
                        totalBytesRead,
                        bytesRead == -1L
                    )
                )
                return bytesRead
            }
        }
}