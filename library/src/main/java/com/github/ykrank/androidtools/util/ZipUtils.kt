package com.github.ykrank.androidtools.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.util.zip.Deflater
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Created by ykrank on 7/17/24
 * 
 */
object ZipUtils {

    fun compressStringByZip(
        inputString: String,
        compressionLevel: Int = Deflater.DEFAULT_COMPRESSION
    ): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val zipOutputStream = ZipOutputStream(byteArrayOutputStream)
        val zipEntry = ZipEntry("0") // 给压缩项目命名
        zipOutputStream.setLevel(compressionLevel) // 设置压缩级别
        zipOutputStream.putNextEntry(zipEntry)

        val stringByteArray = inputString.toByteArray()
        zipOutputStream.write(stringByteArray)

        zipOutputStream.closeEntry()
        zipOutputStream.close()

        return byteArrayOutputStream.toByteArray()
    }

    fun compressStringByGzip(str: String): ByteArray {
        return compressByGzip(str.toByteArray(Charsets.UTF_8))
    }

    fun compressByGzip(str: ByteArray): ByteArray {
        if (str.isEmpty()) return ByteArray(0)

        val baos = ByteArrayOutputStream()
        val gzip = GZIPOutputStream(baos)
        gzip.write(str)
        gzip.close()
        return baos.toByteArray()
    }

    fun decompressGzipToString(compressed: ByteArray): String {
        if (compressed.isEmpty()) return ""

        val gzip = GZIPInputStream(ByteArrayInputStream(compressed))
        val reader = InputStreamReader(gzip, Charsets.UTF_8)
        val sb = StringBuilder()
        val buffer = CharArray(1024)
        var len: Int
        while ((reader.read(buffer).also { len = it }) != -1) {
            sb.appendRange(buffer, 0, len)
        }
        reader.close()
        return sb.toString()
    }
}