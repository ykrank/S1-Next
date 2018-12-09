package me.ykrank.s1next.data.db

import android.database.Cursor
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.data.db.dbmodel.BlackWord
import me.ykrank.s1next.data.db.dbmodel.BlackWordDao
import me.ykrank.s1next.data.db.dbmodel.DaoSession

/**
 * 对黑名单数据库的操作包装
 * Created by AdminYkrank on 2016/2/23.
 */
class BlackWordDbWrapper internal constructor(private val appDaoSessionManager: AppDaoSessionManager) {

    private val blackWordDao: BlackWordDao
        get() = session.blackWordDao

    private val session: DaoSession
        get() = appDaoSessionManager.daoSession

    val blackWordCursor: Single<Cursor>
        get() = Single.just(blackWordDao.queryBuilder())
                .map { builder -> builder.buildCursor().query() }

    fun getAllBlackWord(limit: Int, offset: Int): List<BlackWord> {
        return blackWordDao.queryBuilder()
                .limit(limit)
                .offset(offset)
                .list()
    }

    fun fromBlackWordCursor(cursor: Cursor): BlackWord {
        return blackWordDao.readEntity(cursor, 0)
    }

    fun getBlackWord(word: String): BlackWord? {
        return blackWordDao.queryBuilder()
                .where(BlackWordDao.Properties.Word.eq(word))
                .unique()
    }

    fun saveBlackWord(blackWord: BlackWord) {
        if (blackWord.id == null) {
            blackWordDao.insert(blackWord)
        } else {
            blackWordDao.update(blackWord)
        }
    }

    fun delBlackWord(blackWord: BlackWord) {
        blackWordDao.delete(blackWord)
    }

    fun delBlackWords(blackWords: List<BlackWord>) {
        blackWordDao.deleteInTx(blackWords)
    }

    fun saveDefaultBlackWord(word: String) {
        val blackWord = BlackWord()
        blackWord.word = word
        blackWord.stat = BlackWord.HIDE
        blackWord.timestamp = System.currentTimeMillis()
        saveBlackWord(blackWord)
    }

    companion object {

        val instance: BlackWordDbWrapper
            get() = App.appComponent.blackWordDbWrapper
    }
}
