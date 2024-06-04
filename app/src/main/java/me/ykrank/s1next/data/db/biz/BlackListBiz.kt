package me.ykrank.s1next.data.db.biz

import android.database.Cursor
import android.text.TextUtils
import androidx.collection.LruCache
import me.ykrank.s1next.App.Companion.appComponent
import me.ykrank.s1next.data.db.AppDatabase
import me.ykrank.s1next.data.db.AppDatabaseManager
import me.ykrank.s1next.data.db.dao.BlackListDao
import me.ykrank.s1next.data.db.dbmodel.BlackList

class BlackListBiz(private val manager: AppDatabaseManager) {
    private val blackListDao: BlackListDao
        get() = session.blacklist()
    private val session: AppDatabase
        get() = manager.getOrBuildDb()

    private val idCache: LruCache<Int, BlackList> = LruCache(3000)
    private val nameCache: LruCache<String, BlackList> = LruCache(3000)

    val blackListCursor: Cursor
        get() = blackListDao.loadCursor()

    fun fromBlackListCursor(cursor: Cursor): BlackList {
        return BlackList(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("_id")),
            authorId = cursor.getInt(cursor.getColumnIndexOrThrow("AuthorId")),
            author = cursor.getString(cursor.getColumnIndexOrThrow("Author")),
            post = cursor.getInt(cursor.getColumnIndexOrThrow("Post")),
            forum = cursor.getInt(cursor.getColumnIndexOrThrow("Forum")),
            remark = cursor.getString(cursor.getColumnIndexOrThrow("Remark")),
            timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("Timestamp")),
            upload = cursor.getLong(cursor.getColumnIndexOrThrow("Upload")) > 0,
        )
    }

    private fun readIdCache(id: Int): Pair<Boolean, BlackList?> {
        val cache = idCache.get(id)
        if (cache != null) {
            if (cache === BlackList.EMPTY_BLACKLIST) {
                return Pair(true, null)
            }
            return Pair(true, cache)
        }
        return Pair(false, null)
    }

    private fun readNameCache(name: String): Pair<Boolean, BlackList?> {
        val cache = nameCache.get(name)
        if (cache != null) {
            if (cache === BlackList.EMPTY_BLACKLIST) {
                return Pair(true, null)
            }
            return Pair(true, cache)
        }
        return Pair(false, null)
    }

    private fun refreshCache(blackList: BlackList, del: Boolean = false) {
        if (blackList.authorId > 0) {
            if (del) {
                idCache.put(blackList.authorId, BlackList.EMPTY_BLACKLIST)
            } else {
                idCache.put(blackList.authorId, blackList)
            }
        }
        blackList.author?.apply {
            if (del) {
                nameCache.put(this, BlackList.EMPTY_BLACKLIST)
            } else {
                nameCache.put(this, blackList)
            }
        }
    }


    /**
     * 根据用户id查找记录。有效的只保留一条
     *
     * @param id
     * @return
     */
    private fun getBlackListWithAuthorId(id: Int, enableCache: Boolean = false): BlackList? {
        if (id <= 0) {
            return null
        }
        if (enableCache) {
            val cachePair = readIdCache(id)
            if (cachePair.first) {
                return cachePair.second
            }
        }

        val results: MutableList<BlackList> = blackListDao.getByAuthorId(id).toMutableList()
        if (results.isEmpty()) {
            idCache.put(id, BlackList.EMPTY_BLACKLIST)
            return null
        }
        // 清理相同记录
        var result: BlackList? = null
        // 优先保留同时有id和name的记录
        for (i in results.indices) {
            if (!TextUtils.isEmpty(results[i].author)) {
                result = results.removeAt(i)
                break
            }
        }
        if (result == null) {
            result = results.removeAt(0)
        }
        if (results.isNotEmpty()) {
            blackListDao.delete(results)
        }

        refreshCache(result)
        return result
    }

    /**
     * 根据用户名查找记录。有效的只保留一条
     *
     * @param name
     * @return
     */
    private fun getBlackListWithAuthorName(
        name: String?,
        enableCache: Boolean = false
    ): BlackList? {
        if (name.isNullOrEmpty()) {
            return null
        }
        if (enableCache) {
            val cachePair = readNameCache(name)
            if (cachePair.first) {
                return cachePair.second
            }
        }
        val results: MutableList<BlackList> = blackListDao.getByAuthor(name).toMutableList()
        if (results.isEmpty()) {
            nameCache.put(name, BlackList.EMPTY_BLACKLIST)
            return null
        }
        // 清理相同记录
        var result: BlackList? = null
        // 优先保留同时有id和name的记录
        for (i in results.indices) {
            if (results[i].authorId > 0) {
                result = results.removeAt(i)
                break
            }
        }
        if (result == null) {
            result = results.removeAt(0)
        }
        if (results.isNotEmpty()) {
            blackListDao.delete(results)
        }

        refreshCache(result)
        return result
    }

    /**
     * 合并关联的ID和用户名记录
     * @param id
     * @param name
     * @return
     */
    fun getMergedBlackList(id: Int, name: String?, enableCache: Boolean = false): BlackList? {
        if (id > 0) {
            return getBlackListWithAuthorId(id, enableCache)
        }
        if (!name.isNullOrEmpty()) {
            return getBlackListWithAuthorName(name, enableCache)
        }
        return null
    }

    @BlackList.ForumFLag
    fun getForumFlag(id: Int, name: String?, enableCache: Boolean = false): Int {
        val oBlackList: BlackList? = getMergedBlackList(id, name, enableCache)
        return oBlackList?.forum ?: BlackList.NORMAL
    }

    @BlackList.PostFLag
    fun getPostFlag(id: Int, name: String, enableCache: Boolean = false): Int {
        val oBlackList: BlackList? = getMergedBlackList(id, name, enableCache)
        return oBlackList?.post ?: BlackList.NORMAL
    }

    fun saveBlackList(blackList: BlackList) {
        if (blackList.authorId <= 0 && TextUtils.isEmpty(blackList.author)) {
            return
        }
        val oBlackList: BlackList? = getMergedBlackList(blackList.authorId, blackList.author)
        if (oBlackList == null) {
            blackListDao.insert(listOf(blackList))
        } else {
            oBlackList.mergeFrom(blackList)
            blackListDao.update(listOf(oBlackList))
        }
        refreshCache(blackList)
    }

    fun delBlackList(blackList: BlackList) {
        val oBlackList: BlackList? = getMergedBlackList(blackList.authorId, blackList.author)
        if (oBlackList != null) {
            blackListDao.delete(listOf(oBlackList))
        }
        refreshCache(blackList, del = true)
    }

    fun delBlackLists(blackLists: List<BlackList>) {
        blackLists.forEach {
            refreshCache(it, del = true)
        }
        blackListDao.delete(blackLists)
    }

    fun saveDefaultBlackList(authorid: Int, author: String?, remark: String?) {
        val blackList = BlackList()
        blackList.authorId = authorid
        blackList.author = author
        blackList.remark = remark
        blackList.post = BlackList.HIDE_POST
        blackList.forum = BlackList.HIDE_FORUM
        blackList.timestamp = System.currentTimeMillis()
        saveBlackList(blackList)
    }

    fun delDefaultBlackList(authorid: Int, author: String?) {
        val blackList = BlackList()
        blackList.authorId = authorid
        blackList.author = author
        delBlackList(blackList)
    }

    companion object {
        fun getInstance(): BlackListBiz {
            return appComponent.blackListBiz
        }
    }
}