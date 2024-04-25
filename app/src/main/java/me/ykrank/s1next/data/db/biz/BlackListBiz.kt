package me.ykrank.s1next.data.db.biz

import android.database.Cursor
import android.text.TextUtils
import me.ykrank.s1next.App.Companion.appComponent
import me.ykrank.s1next.data.db.AppDatabase
import me.ykrank.s1next.data.db.AppDatabaseManager
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.data.db.dao.BlackListDao
import me.ykrank.s1next.data.db.dbmodel.BlackList

class BlackListBiz(private val manager: AppDatabaseManager) {
    private val blackListDao: BlackListDao
        get() = session.blacklist()
    private val session: AppDatabase
        get() = manager.getOrBuildDb()

    fun getAllBlackList(limit: Int, offset: Int): List<BlackList> {
        return blackListDao.loadLimit(limit, offset)
    }

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

    /**
     * 根据用户id查找记录。有效的只保留一条
     *
     * @param id
     * @return
     */
    private fun getBlackListWithAuthorId(id: Int): BlackList? {
        if (id <= 0) {
            return null
        }
        val results: MutableList<BlackList> = blackListDao.getByAuthorId(id).toMutableList()
        if (results.isEmpty()) {
            return null
        }
        var result: BlackList? = null
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
        return result
    }

    /**
     * 根据用户名查找记录。有效的只保留一条
     *
     * @param name
     * @return
     */
    private fun getBlackListWithAuthorName(name: String?): BlackList? {
        if (name.isNullOrEmpty()) {
            return null
        }
        val results: MutableList<BlackList> = blackListDao.getByAuthor(name).toMutableList()
        if (results.isEmpty()) {
            return null
        }
        var result: BlackList? = null
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
        return result
    }

    /**
     * 合并关联的ID和用户名记录
     * @param id
     * @param name
     * @return
     */
    fun getMergedBlackList(id: Int, name: String?): BlackList? {
        if (id <= 0) {
            return getBlackListWithAuthorName(name)
        }
        if (name.isNullOrEmpty()) {
            return getBlackListWithAuthorId(id)
        }
        val results: MutableList<BlackList> =
            blackListDao.getByAuthorAndId(id, name).toMutableList()
        if (results.isEmpty()) {
            return null
        }
        var result: BlackList? = null
        var tempResult: BlackList
        for (i in results.indices) {
            tempResult = results[i]
            if (tempResult.authorId == id && TextUtils.equals(tempResult.author, name)) {
                result = results.removeAt(i)
                break
            }
        }
        if (result == null) {
            result = results.removeAt(0)
            result.authorId = id
            result.author = name
            blackListDao.update(listOf(result))
        }
        if (results.isNotEmpty()) {
            blackListDao.delete(results)
        }
        return result
    }

    @BlackList.ForumFLag
    fun getForumFlag(id: Int, name: String?): Int {
        val oBlackList: BlackList? = getMergedBlackList(id, name)
        return oBlackList?.forum ?: BlackList.NORMAL
    }

    @BlackList.PostFLag
    fun getPostFlag(id: Int, name: String): Int {
        val oBlackList: BlackList? = getMergedBlackList(id, name)
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
    }

    fun delBlackList(blackList: BlackList) {
        val oBlackList: BlackList? = getMergedBlackList(blackList.authorId, blackList.author)
        if (oBlackList != null) {
            blackListDao.delete(listOf(oBlackList))
        }
    }

    fun delBlackLists(blackLists: List<BlackList>) {
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