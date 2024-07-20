package me.ykrank.s1next.data.db.biz

import androidx.annotation.WorkerThread
import me.ykrank.s1next.App
import me.ykrank.s1next.data.db.AppDatabase
import me.ykrank.s1next.data.db.AppDatabaseManager
import me.ykrank.s1next.data.db.dao.ReadProgressDao
import me.ykrank.s1next.data.db.dbmodel.ReadProgress

class ReadProgressBiz(private val manager: AppDatabaseManager) {

    private val readProgressDao: ReadProgressDao
        get() = session.readProgress()

    private val session: AppDatabase
        get() = manager.getOrBuildDb()

    @WorkerThread
    fun getWithThreadId(threadId: Int): ReadProgress? {
        return readProgressDao.getByThreadId(threadId)
    }

    @WorkerThread
    fun saveReadProgress(readProgress: ReadProgress) {
        val oReadProgress = getWithThreadId(readProgress.threadId)
        if (oReadProgress == null) {
            readProgressDao.insert(readProgress)
        } else {
            oReadProgress.copyFrom(readProgress)
            readProgressDao.update(oReadProgress)
        }
    }

    fun saveReadProgressAsync(readProgress: ReadProgress) {
        manager.runAsync {
            saveReadProgress(readProgress)
        }
    }

    @WorkerThread
    fun delReadProgress(threadId: Int) {
        val oReadProgress = getWithThreadId(threadId)
        if (oReadProgress != null) {
            readProgressDao.delete(oReadProgress)
        }
    }

    companion object {

        val instance: ReadProgressBiz
            get() = App.appComponent.readProgressBiz
    }
}