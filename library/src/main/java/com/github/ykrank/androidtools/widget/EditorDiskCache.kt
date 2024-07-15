package com.github.ykrank.androidtools.widget

import android.util.LruCache
import androidx.annotation.WorkerThread
import com.bumptech.glide.disklrucache.DiskLruCache
import com.bumptech.glide.load.Key
import com.bumptech.glide.signature.ObjectKey
import com.bumptech.glide.util.Util
import com.github.ykrank.androidtools.util.L.report
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * use to cache document in editing
 */
@WorkerThread
class EditorDiskCache(cachePathName: String) {
    /**
     * We use both disk cache and memory cache.
     */
    private lateinit var diskLruCache: DiskLruCache
    private val lruCache: LruCache<String, String?> = LruCache(MEMORY_CACHE_MAX_NUMBER)
    private val keyGenerator: KeyGenerator

    init {
        val file = File(
            cachePathName
                    + File.separator + DISK_CACHE_DIRECTORY
        )
        diskLruCache = try {
            DiskLruCache.open(
                file, APP_VERSION, 1,
                DISK_CACHE_MAX_SIZE
            )
        } catch (e: IOException) {
            throw RuntimeException("Failed to open the cache in $file.", e)
        }
        keyGenerator = KeyGenerator()
    }

    operator fun get(key: String?): String? {
        if (key.isNullOrEmpty()) {
            return null
        }
        val encodedKey = keyGenerator.getKey(key)
        var result: String?
        result = lruCache[encodedKey]
        if (result == null) {
            try {
                synchronized(DISK_CACHE_LOCK) {
                    val value = diskLruCache[encodedKey]
                    if (value != null) {
                        result = value.getString(0)
                    }
                }
            } catch (ignore: IOException) {
                result = null
            }
        }
        return result
    }

    /**
     * if value is empty, then remove cache
     */
    fun put(key: String?, value: String?) {
        if (key.isNullOrEmpty()) {
            return
        }
        if (value.isNullOrEmpty()) {
            remove(key)
            return
        }
        val encodedKey = keyGenerator.getKey(key)
        lruCache.put(encodedKey, value)
        try {
            synchronized(DISK_CACHE_LOCK) {
                val editor = diskLruCache.edit(encodedKey)
                // Editor will be null if there are two concurrent puts. In the worst case we will just silently fail.
                if (editor != null) {
                    var writer: BufferedWriter? = null
                    try {
                        val file = editor.getFile(0)
                        writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file), "UTF-8"))
                        writer.write(value)
                        editor.commit()
                    } finally {
                        editor.abortUnlessCommitted()
                        if (writer != null) {
                            try {
                                writer.close()
                            } catch (e: IOException) {
                                report(e)
                            }
                        }
                    }
                }
            }
        } catch (ignore: IOException) {
        }
    }

    fun remove(key: String?) {
        if (key.isNullOrEmpty()) {
            return
        }
        val encodedKey = keyGenerator.getKey(key)
        lruCache.remove(encodedKey)
        try {
            synchronized(DISK_CACHE_LOCK) { diskLruCache.remove(encodedKey) }
        } catch (ignore: IOException) {
        }
    }

    private class KeyGenerator {
        private val lruCache = LruCache<String?, String?>(KEYS_MEMORY_CACHE_MAX_NUMBER)
        fun getKey(value: String): String? {
            var safeKey: String?
            synchronized(lruCache) { safeKey = lruCache[value] }
            if (safeKey == null) {
                try {
                    val key: Key = ObjectKey(value)
                    val messageDigest = MessageDigest.getInstance("SHA-256")
                    key.updateDiskCacheKey(messageDigest)
                    safeKey = Util.sha256BytesToHex(messageDigest.digest())
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }
                synchronized(lruCache) { lruCache.put(value, safeKey) }
            }
            return safeKey
        }

        companion object {
            private const val KEYS_MEMORY_CACHE_MAX_NUMBER = 1000
        }
    }

    companion object {
        private const val APP_VERSION = 1
        private const val MEMORY_CACHE_MAX_NUMBER = 4
        private const val DISK_CACHE_DIRECTORY = "editor_disk_cache"
        private const val DISK_CACHE_MAX_SIZE = (100 * 1000 // 100KB
                ).toLong()
        private val DISK_CACHE_LOCK = Any()
    }
}
