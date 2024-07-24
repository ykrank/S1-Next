package me.ykrank.s1next.data.cache.biz

import me.ykrank.s1next.data.cache.CacheDatabase
import me.ykrank.s1next.data.cache.CacheDatabaseManager
import me.ykrank.s1next.data.cache.dao.CacheGroupDao
import me.ykrank.s1next.data.cache.dbmodel.CacheGroup
import me.ykrank.s1next.data.cache.exmodel.CacheGroupExtra
import me.ykrank.s1next.data.cache.exmodel.CacheGroupModel

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
        groups: List<String>,
        extras: List<String>,
    ) {
        manager.runAsync {
            val group = CacheGroupModel(groups)
            val extra = CacheGroupExtra(extras)
            saveTitle(
                CacheGroup(
                    title,
                    group.group,
                    group.group1,
                    group.group2,
                    group.group3
                ).apply {
                    this.extra = extra.extra
                    this.extra1 = extra.extra1
                    this.extra2 = extra.extra2
            })
        }
    }

    fun query(
        groups: List<String>
    ): CacheGroup? {
        val group = CacheGroupModel(groups)
        return cacheGroupDao.query(group.group, group.group1, group.group2, group.group3)
    }

    companion object {
        const val TAG = "CacheGroupBiz"
    }
}