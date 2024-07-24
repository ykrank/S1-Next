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
        cacheGroup: CacheGroup
    ) {
        val oldCacheGroup = cacheGroupDao.query(
            cacheGroup.group,
            cacheGroup.group1,
            cacheGroup.group2,
            cacheGroup.group3
        )
        if (oldCacheGroup != null) {
            oldCacheGroup.copyFrom(cacheGroup)
            cacheGroupDao.update(oldCacheGroup)
        } else {
            cacheGroupDao.insert(cacheGroup)
        }
    }

    fun saveTitleAsync(
        title: String,
        group: String,
        group1: String? = null,
        group2: String? = null,
        group3: String? = null,
        extra: String? = null,
        extra1: String? = null,
        extra2: String? = null,
    ) {
        manager.runAsync {
            saveTitle(CacheGroup(title, group, group1, group2, group3).apply {
                this.extra = extra
                this.extra1 = extra1
                this.extra2 = extra2
            })
        }
    }

    companion object {
        const val TAG = "CacheGroupBiz"
    }
}