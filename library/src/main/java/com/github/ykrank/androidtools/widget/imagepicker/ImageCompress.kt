package com.github.ykrank.androidtools.widget.imagepicker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.WorkerThread
import com.github.ykrank.androidtools.extension.stream
import java.io.OutputStream
import kotlin.math.min


/**
 * Created by ykrank on 11/8/24
 */
object ImageCompress {

    @WorkerThread
    @Throws(Exception::class)
    fun compressImage(
        context: Context,
        inputFile: Uri,
        outputFile: OutputStream,
        reqSize: Size? = null,
    ) {
        val options = BitmapFactory.Options()
        inputFile.stream(context)?.use { inputStream ->
            if (reqSize != null) {
                // 第一步：获取图片的尺寸
                options.inJustDecodeBounds = true // 只获取尺寸，不加载 Bitmap
                BitmapFactory.decodeStream(inputStream, null, options)

                // 计算合适的 inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqSize)
                options.inJustDecodeBounds = false // 重新加载 Bitmap
            }

            // 第二步：加载降采样后的 Bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream, null, options) ?: return

            // 第三步：压缩 Bitmap
            try {
                // 质量范围是 0 - 100
                val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    Bitmap.CompressFormat.WEBP
                }
                bitmap.compress(format, 85, outputFile)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // 释放 Bitmap 占用的内存
                bitmap.recycle()
            }
        }
    }

    // 计算合适的 inSampleSize
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqSize: Size): Int {
        // 源图片的宽高
        val height = options.outHeight
        val width = options.outWidth
        return min(width / reqSize.width, height / reqSize.height)
    }
}