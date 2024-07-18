package me.ykrank.s1next.data.db.biz

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

    fun saveTextZip(key: String, content: String) {
        val zipBlob = ZipUtils.compressStringByGzip(content)
        val cache = Cache().apply {
            this.key = key
            this.blob = zipBlob
            this.timestamp = System.currentTimeMillis()
        }
        cacheDao.insert(cache)
    }

    fun getTextZipByKey(key: String): Cache? {
        return cacheDao.getByKey(key)?.apply {
            this.text = this.blob?.let {
                ZipUtils.decompressGzipToString(it)
            }
        }
    }
}