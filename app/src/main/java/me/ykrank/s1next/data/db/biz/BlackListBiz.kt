package me.ykrank.s1next.data.db.biz

import android.database.Cursor
import android.text.TextUtils
import io.reactivex.Single
import me.ykrank.s1next.data.db.AppDatabase
import me.ykrank.s1next.data.db.AppDatabaseManager
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

    val blackListCursor: Single<Cursor>
        get() = Single.just(blackListDao.queryBuilder())
            .map<Cursor>(Function<T, Cursor> { builder: T -> builder.buildCursor().query() })

    fun fromBlackListCursor(cursor: Cursor): BlackList {
        return blackListDao.readEntity(cursor, 0)
    }

    /**
     * 根据用户id查找记录。有效的只保留一条
     *
     * @param id
     * @return
     */
    fun getBlackListWithAuthorId(id: Int): BlackList? {
        if (id <= 0) {
            return null
        }
        val results: List<BlackList> = blackListDao.queryBuilder()
            .where(Properties.AuthorId.eq(id))
            .list()
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
        if (!results.isEmpty()) {
            blackListDao.deleteInTx(results)
        }
        return result
    }

    /**
     * 根据用户名查找记录。有效的只保留一条
     *
     * @param name
     * @return
     */
    fun getBlackListWithAuthorName(name: String?): BlackList? {
        if (TextUtils.isEmpty(name)) {
            return null
        }
        val results: List<BlackList> = blackListDao.queryBuilder()
            .where(Properties.Author.eq(name))
            .list()
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
        if (!results.isEmpty()) {
            blackListDao.deleteInTx(results)
        }
        return result
    }

    /**
     * 合并关联的ID和用户名记录
     * @param id
     * @param name
     * @return
     */
    fun getMergedBlackList(id: Int, name: String): BlackList? {
        if (id <= 0) {
            return getBlackListWithAuthorName(name)
        }
        if (TextUtils.isEmpty(name)) {
            return getBlackListWithAuthorId(id)
        }
        val results: List<BlackList> = blackListDao.queryBuilder()
            .whereOr(Properties.AuthorId.eq(id), Properties.Author.eq(name))
            .list()
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
            blackListDao.update(result)
        }
        if (!results.isEmpty()) {
            blackListDao.deleteInTx(results)
        }
        return result
    }

    @ForumFLag
    fun getForumFlag(id: Int, name: String): Int {
        val oBlackList: BlackList? = getMergedBlackList(id, name)
        return if (oBlackList != null) oBlackList.forum else BlackList.NORMAL
    }

    @PostFLag
    fun getPostFlag(id: Int, name: String): Int {
        val oBlackList: BlackList? = getMergedBlackList(id, name)
        return if (oBlackList != null) oBlackList.post else BlackList.NORMAL
    }

    fun saveBlackList(blackList: BlackList) {
        if (blackList.authorId <= 0 && TextUtils.isEmpty(blackList.author)) {
            return
        }
        val oBlackList: BlackList? = getMergedBlackList(blackList.authorId, blackList.author)
        if (oBlackList == null) {
            blackListDao.insert(blackList)
        } else {
            oBlackList.mergeFrom(blackList)
            blackListDao.update(oBlackList)
        }
    }

    fun delBlackList(blackList: BlackList) {
        val oBlackList: BlackList? = getMergedBlackList(blackList.authorId, blackList.author)
        if (oBlackList != null) {
            blackListDao.delete(oBlackList)
        }
    }

    fun delBlackLists(blackLists: List<BlackList?>?) {
        blackListDao.deleteInTx(blackLists)
    }

    fun saveDefaultBlackList(authorid: Int, author: String, remark: String) {
        val blackList = BlackList()
        blackList.authorId = authorid
        blackList.author = author
        blackList.remark = remark
        blackList.post = BlackList.HIDE_POST
        blackList.forum = BlackList.HIDE_FORUM
        blackList.timestamp = System.currentTimeMillis()
        saveBlackList(blackList)
    }

    fun delDefaultBlackList(authorid: Int, author: String) {
        val blackList = BlackList()
        blackList.authorId = authorid
        blackList.author = author
        delBlackList(blackList)
    }
}