package me.ykrank.s1next.data.cache.biz

import androidx.annotation.WorkerThread
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.ZipUtils
import me.ykrank.s1next.App
import me.ykrank.s1next.data.cache.CacheConstants
import me.ykrank.s1next.data.cache.CacheDatabase
import me.ykrank.s1next.data.cache.CacheDatabaseManager
import me.ykrank.s1next.data.cache.dao.CacheDao
import me.ykrank.s1next.data.cache.dbmodel.Cache

/**
 * Created by ykrank on 7/17/24
 * 
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

    /**
     * 1. 如果传入blob，则将blob压缩为zip。
     * 2. 否则将传入的decodeZipString压缩为zip
     */
    @WorkerThread
    private fun saveZip(
        cache: Cache,
        maxSize: Int = DEFAULT_MAX_SIZE
    ) {
        val content = cache.blob ?: cache.decodeZipString?.toByteArray(Charsets.UTF_8)
        if (content != null) {
            val start = System.currentTimeMillis()
            val gzipBlob = ZipUtils.compressByGzip(content)
            val gzipTime = System.currentTimeMillis() - start
            L.i(
                TAG,
                "saveTextZip: ${cache.group} $${cache.key} s${content.size}->${gzipBlob.size} t${gzipTime}, max:${maxSize}"
            )
            cache.blob = gzipBlob
        }
        cacheDao.insert(cache)
        if (count > maxSize) {
            cacheDao.deleteNotTopRecords(maxSize)
        }
    }

    /**
     * 注意content必须是不可修改的，避免异步问题
     */
    fun saveZipAsync(
        key: String,
        uid: Int?,
        content: Any,
        title: String? = null,
        group: String = CacheConstants.GROUP_EMPTY,
        maxSize: Int = DEFAULT_MAX_SIZE,
        group1: String? = null,
        group2: String? = null,
        group3: String? = null,
    ) {
        manager.runAsync {
            saveZip(
                Cache(
                    key,
                    uid = uid,
                    title = title,
                    group = group,
                    decodeZipString = if (content is String) {
                        content
                    } else {
                        objectMapper.writeValueAsString(content)
                    }
                ).apply {
                    if (group1 != null) {
                        this.group1 = group1
                    }
                    if (group2 != null) {
                        this.group2 = group2
                    }
                    if (group3 != null) {
                        this.group3 = group3
                    }
                }, maxSize
            )
        }
    }

    /**
     * 查询缓存，并解码zip blob
     */
    @WorkerThread
    fun getTextZipByKey(key: String): Cache? {
        return cacheDao.getByKey(key)?.apply {
            this.decodeZipString = this.blob?.let {
                ZipUtils.decompressGzipToString(it)
            }
        }
    }

    companion object {
        const val TAG = "CacheBiz"
        const val DEFAULT_MAX_SIZE = 1000
    }
}