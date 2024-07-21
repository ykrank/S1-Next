package me.ykrank.s1next.data.cache

import androidx.annotation.WorkerThread
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ykrank.androidtools.util.FileUtil
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.ZipUtils
import me.ykrank.s1next.App

/**
 * Created by yuanke on 7/17/24
 * @author yuanke.ykrank@bytedance.com
 */
class CacheBiz(private val manager: CacheDatabaseManager, private val objectMapper: ObjectMapper) {

    private val cacheDao: CacheDao
        get() = session.cache()

    private val session: CacheDatabase
        get() = manager.getOrBuildDb()

    val count
        @WorkerThread
        get() = cacheDao.getCount()

    val size
        @WorkerThread
        get() = App.get().getDatabasePath(CacheDatabase.DB_NAME).length()

    @WorkerThread
    private fun saveZip(
        key: String,
        content: ByteArray,
        group: String = DEFAULT_GROUP,
        maxSize: Int = DEFAULT_MAX_SIZE
    ) {
        val start = System.currentTimeMillis()
        val gzipBlob = ZipUtils.compressByGzip(content)
        val gzipTime = System.currentTimeMillis() - start
        L.i(
            TAG,
            "saveTextZip: $group $key s${content.size}->${gzipBlob.size} t${gzipTime}, max:${maxSize}"
        )
        val cache = Cache().apply {
            this.key = key
            this.blob = gzipBlob
            this.timestamp = System.currentTimeMillis()
        }
        cacheDao.insert(cache)
        cacheDao.deleteNotTopRecords(group, maxSize)
    }

    @WorkerThread
    private fun saveTextZip(
        key: String,
        content: String,
        group: String = DEFAULT_GROUP,
        maxSize: Int = DEFAULT_MAX_SIZE
    ) {
        saveZip(key, content.toByteArray(Charsets.UTF_8), group, maxSize)
    }

    fun saveTextZipAsync(
        key: String,
        content: String,
        group: String = DEFAULT_GROUP,
        maxSize: Int = DEFAULT_MAX_SIZE
    ) {
        manager.runAsync {
            saveTextZip(key, content, group, maxSize)
        }
    }

    fun saveZipAsync(
        key: String,
        content: Any,
        group: String = DEFAULT_GROUP,
        maxSize: Int = DEFAULT_MAX_SIZE
    ) {
        manager.runAsync {
            saveTextZip(key, objectMapper.writeValueAsString(content), group, maxSize)
        }
    }

    @WorkerThread
    fun getTextZipByKey(key: String): Cache? {
        return cacheDao.getByKey(key)?.apply {
            this.text = this.blob?.let {
                ZipUtils.decompressGzipToString(it)
            }
        }
    }

    companion object {
        const val TAG = "CacheBiz"
        const val DEFAULT_GROUP = "default"
        const val DEFAULT_MAX_SIZE = 1000
    }
}