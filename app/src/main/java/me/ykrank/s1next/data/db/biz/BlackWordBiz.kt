package me.ykrank.s1next.data.db.biz

import android.database.Cursor
import me.ykrank.s1next.App
import me.ykrank.s1next.data.db.AppDatabase
import me.ykrank.s1next.data.db.AppDatabaseManager
import me.ykrank.s1next.data.db.dao.BlackWordDao
import me.ykrank.s1next.data.db.dbmodel.BlackWord

/**
 * 对黑名单数据库的操作包装
 */
class BlackWordBiz(private val manager: AppDatabaseManager) {

    private val blackWordDao: BlackWordDao
        get() = session.blackWord()

    private val session: AppDatabase
        get() = manager.getOrBuildDb()

    val blackWordCursor: Cursor
        get() = blackWordDao.loadCursor()

    fun getAllBlackWord(limit: Int, offset: Int): List<BlackWord> {
        return blackWordDao.loadLimit(limit, offset)
    }

    fun getAllNotNormalBlackWord(): List<BlackWord> {
        return blackWordDao.loadNotNormal()
    }

    fun fromBlackWordCursor(cursor: Cursor): BlackWord {
        // TODO: 从Cursor中读取
        return BlackWord()
    }

    fun count(): Int {
        return blackWordDao.getCount()
    }

    fun getBlackWord(word: String): BlackWord? {
        return blackWordDao.getByWord(word)
    }

    fun saveBlackWord(blackWord: BlackWord) {
        if (blackWord.id == null) {
            blackWordDao.insert(blackWord)
        } else {
            blackWordDao.update(blackWord)
        }
    }

    fun delBlackWord(blackWord: BlackWord) {
        blackWordDao.delete(listOf(blackWord))
    }

    fun delBlackWords(blackWords: List<BlackWord>) {
        blackWordDao.delete(blackWords)
    }

    fun saveDefaultBlackWord(word: String) {
        val blackWord = BlackWord()
        blackWord.word = word
        blackWord.stat = BlackWord.HIDE
        blackWord.timestamp = System.currentTimeMillis()
        saveBlackWord(blackWord)
    }

    companion object {

        val instance: BlackWordBiz
            get() = App.appComponent.blackWordDbWrapper
    }
}
