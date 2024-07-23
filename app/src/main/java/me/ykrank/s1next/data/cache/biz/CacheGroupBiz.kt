package me.ykrank.s1next.data.cache.biz

import me.ykrank.s1next.data.cache.CacheDatabase
import me.ykrank.s1next.data.cache.CacheDatabaseManager
import me.ykrank.s1next.data.cache.dao.CacheGroupDao
import me.ykrank.s1next.data.cache.dbmodel.CacheGroup

/**
 * Created by ykrank on 7/17/24
 *
 */
class CacheGroupBiz(
    private val manager: CacheDatabaseManager
) {

    private val cacheGroupDao: CacheGroupDao
        get() = session.cacheGroup()

    private val session: CacheDatabase
        get() = manager.getOrBuildDb()

    private fun saveTitle(
        title: String,
        group: String,
        group1: String? = null,
        group2: String? = null,
        group3: String? = null,
    ) {
        val oldCacheGroup = cacheGroupDao.query(group, group1 ?: "", group2 ?: "", group3 ?: "")
        if (oldCacheGroup != null) {
            oldCacheGroup.apply {
                this.title = title
            }
            cacheGroupDao.update(oldCacheGroup)
        } else {
            cacheGroupDao.insert(CacheGroup(title, group, group1, group2, group3))
        }
    }

    fun saveTitleAsync(
        title: String,
        group: String,
        group1: String? = null,
        group2: String? = null,
        group3: String? = null,
    ) {
        manager.runAsync {
            saveTitle(title, group, group1, group2, group3)
        }
    }

    companion object {
        const val TAG = "CacheGroupBiz"
    }
}