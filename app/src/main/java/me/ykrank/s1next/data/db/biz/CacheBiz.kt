package me.ykrank.s1next.data.db.biz

import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.ZipUtils
import me.ykrank.s1next.data.db.AppDatabase
import me.ykrank.s1next.data.db.AppDatabaseManager
import me.ykrank.s1next.data.db.dao.CacheDao
import me.ykrank.s1next.data.db.dbmodel.Cache

/**
 * Created by yuanke on 7/17/24
 * @author yuanke.ykrank@bytedance.com
 */
class CacheBiz(private val manager: AppDatabaseManager) {

    private val cacheDao: CacheDao
        get() = session.cache()

    private val session: AppDatabase
        get() = manager.getOrBuildDb()

    fun saveTextZip(
        key: String,
        content: String,
        group: String = DEFAULT_GROUP,
        maxSize: Int = DEFAULT_MAX_SIZE
    ) {
        val start = System.currentTimeMillis()
        val gzipBlob = ZipUtils.compressStringByGzip(content)
        val gzipTime = System.currentTimeMillis() - start
        L.i(
            TAG,
            "saveTextZip: $group $key s${content.length}->${gzipBlob.size} t${gzipTime}, max:${maxSize}"
        )
        val cache = Cache().apply {
            this.key = key
            this.blob = gzipBlob
            this.timestamp = System.currentTimeMillis()
        }
        cacheDao.insert(cache)
        cacheDao.deleteNotTopRecords(group, maxSize)
    }

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