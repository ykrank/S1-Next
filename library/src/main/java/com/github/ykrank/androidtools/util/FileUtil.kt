package com.github.ykrank.androidtools.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.ImageHeaderParser.ImageType
import com.bumptech.glide.load.ImageHeaderParserUtils
import com.github.ykrank.androidtools.util.L.e
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.reduce
import okio.BufferedSink
import okio.BufferedSource
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.OutputStream
import java.text.DecimalFormat

/**
 * Created by ykrank on 2017/6/6.
 */
object FileUtil {
    private var dfTwoDecimal = DecimalFormat("0.00")

    @Throws(IOException::class)
    fun copyFile(source: File, sink: File) {
        var bufferedSource: BufferedSource? = null
        var bufferedSink: BufferedSink? = null
        try {
            bufferedSource = source.source().buffer()
            bufferedSink = sink.sink().buffer()
            bufferedSink.writeAll(bufferedSource)
        } finally {
            if (bufferedSink != null) {
                try {
                    bufferedSink.close()
                } catch (e: IOException) {
                    e(e)
                }
            }
            if (bufferedSource != null) {
                try {
                    bufferedSource.close()
                } catch (e: IOException) {
                    e(e)
                }
            }
        }
    }

    @Throws(IOException::class)
    fun copyFile(source: File, outputStream: OutputStream) {
        var bufferedSource: BufferedSource? = null
        var bufferedSink: BufferedSink? = null
        try {
            bufferedSource = source.source().buffer()
            bufferedSink = outputStream.sink().buffer()
            bufferedSink.writeAll(bufferedSource)
        } finally {
            if (bufferedSink != null) {
                try {
                    bufferedSink.close()
                } catch (e: IOException) {
                    e(e)
                }
            }
            if (bufferedSource != null) {
                try {
                    bufferedSource.close()
                } catch (e: IOException) {
                    e(e)
                }
            }
        }
    }

    fun notifyImageInMediaStore(context: Context, file: File) {
        notifyImageInMediaStore(context, Uri.parse("file://" + file.absolutePath))
    }

    fun notifyImageInMediaStore(context: Context, uri: Uri) {
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
    }

    @Throws(IOException::class)
    fun getImageType(context: Context, file: File): ImageType {
        val parsers = Glide.get(context).registry.getImageHeaderParsers()
        val arrayPool = Glide.get(context).arrayPool
        return ImageHeaderParserUtils.getType(parsers, FileInputStream(file), arrayPool)
    }

    fun getImageTypeSuffix(imageType: ImageType?): String? {
        return when (imageType) {
            ImageType.JPEG -> ".jpg"
            ImageType.GIF -> ".gif"
            ImageType.PNG, ImageType.PNG_A -> ".png"
            ImageType.RAW -> ".raw"
            ImageType.WEBP, ImageType.WEBP_A -> ".webp"
            else -> null
        }
    }

    fun getPrintSize(sizeIn: Long, df: DecimalFormat = dfTwoDecimal): String {
        var size = sizeIn
        if (size < 1024) {
            return size.toString() + "B"
        }
        size /= 1024
        if (size < 1024) {
            return size.toString() + "KB"
        }
        var dSize = (size / 1024.0f).toDouble()
        if (dSize < 1024) {
            return df.format(dSize) + "MB"
        }
        dSize /= 1024.0f
        return df.format(dSize) + "GB"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getFolderSizeFlow(directory: File): Flow<Long> = flow {
        if (directory.isDirectory) {
            val files = directory.listFiles() ?: emptyArray<File>()
            // 使用 Flow 并行处理每个文件和子目录
            emitAll(files.asFlow().flatMapMerge { file ->
                if (file.isFile) {
                    flowOf(file.length()) // 返回文件大小
                } else {
                    getFolderSizeFlow(file) // 递归调用
                }
            })
        }
    }

    suspend fun calculateTotalSize(directory: File): Long {
        return getFolderSizeFlow(directory)
            .reduce { accumulator, size -> accumulator + size } // 累加所有大小
    }
}
