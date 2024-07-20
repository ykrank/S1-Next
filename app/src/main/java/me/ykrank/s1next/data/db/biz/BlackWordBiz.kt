package me.ykrank.s1next.data.db.biz

import android.database.Cursor
import androidx.annotation.WorkerThread
import me.ykrank.s1next.App
import me.ykrank.s1next.data.db.AppDatabase
import me.ykrank.s1next.data.db.AppDatabaseManager
import me.ykrank.s1next.data.db.dao.BlackWordDao
import me.ykrank.s1next.data.db.dbmodel.BlackWord

/**
 * 对黑名单数据库的操作包装
 */
class BlackWordBiz(private val manager: AppDatabaseManager) {

    private var cache: List<BlackWord>? = null

    private val blackWordDao: BlackWordDao
        get() = session.blackWord()

    private val session: AppDatabase
        get() = manager.getOrBuildDb()

    val blackWordCursor: Cursor
        @WorkerThread
        get() = blackWordDao.loadCursor()

    @WorkerThread
    fun getAllNotNormalBlackWord(): List<BlackWord> {
        val tCache = cache
        if (tCache != null) {
            return tCache
        }
        return blackWordDao.loadNotNormal().apply {
            cache = this
        }
    }

    fun fromBlackWordCursor(cursor: Cursor): BlackWord {
        return BlackWord(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("_id")),
            word = cursor.getString(cursor.getColumnIndexOrThrow("Word")),
            stat = cursor.getInt(cursor.getColumnIndexOrThrow("Stat")),
            timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("Timestamp")),
            upload = cursor.getLong(cursor.getColumnIndexOrThrow("Upload")) > 0,
        )
    }

    @WorkerThread
    fun count(): Int {
        return blackWordDao.getCount()
    }

    @WorkerThread
    fun getBlackWord(word: String): BlackWord? {
        return blackWordDao.getByWord(word)
    }

    @WorkerThread
    fun saveBlackWord(blackWord: BlackWord) {
        cache = null
        if (blackWord.id == null) {
            blackWordDao.insert(blackWord)
        } else {
            blackWordDao.update(blackWord)
        }
    }

    @WorkerThread
    fun delBlackWord(blackWord: BlackWord) {
        cache = null
        blackWordDao.delete(listOf(blackWord))
    }

    @WorkerThread
    fun delBlackWords(blackWords: List<BlackWord>) {
        cache = null
        blackWordDao.delete(blackWords)
    }

    @WorkerThread
    fun saveDefaultBlackWord(word: String) {
        val blackWord = BlackWord()
        blackWord.word = word
        blackWord.stat = BlackWord.HIDE
        blackWord.timestamp = System.currentTimeMillis()
        saveBlackWord(blackWord)
    }

    companion object {

        val instance: BlackWordBiz
            get() = App.appComponent.blackWordBiz
    }
}
