package com.github.ykrank.androidtools.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Created by yuanke on 7/17/24
 * @author yuanke.ykrank@bytedance.com
 */
object ZipUtils {

    fun compressStringByGzip(str: String): ByteArray {
        if (str.isEmpty()) return ByteArray(0)

        val baos = ByteArrayOutputStream()
        val gzip = GZIPOutputStream(baos)
        gzip.write(str.toByteArray(Charsets.UTF_8))
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