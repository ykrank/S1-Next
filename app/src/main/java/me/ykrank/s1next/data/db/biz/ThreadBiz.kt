package me.ykrank.s1next.data.db.biz

import androidx.annotation.WorkerThread
import me.ykrank.s1next.App
import me.ykrank.s1next.data.db.AppDatabase
import me.ykrank.s1next.data.db.AppDatabaseManager
import me.ykrank.s1next.data.db.dbmodel.DbThread

class ThreadBiz(private val manager: AppDatabaseManager) {

    private val threadDao
        get() = session.thread()

    private val session: AppDatabase
        get() = manager.getOrBuildDb()

    @WorkerThread
    fun getWithThreadId(threadId: Int): DbThread? {
        return threadDao.getByThreadId(threadId)
    }

    @WorkerThread
    fun saveThread(dbThread: DbThread) {
        val oDbThread = getWithThreadId(dbThread.threadId)
        if (oDbThread == null) {
            threadDao.insert(dbThread)
        } else {
            oDbThread.copyFrom(dbThread)
            threadDao.update(oDbThread)
        }
    }

    @WorkerThread
    fun delThread(threadId: Int) {
        val oDbThread = getWithThreadId(threadId)
        if (oDbThread != null) {
            threadDao.delete(oDbThread)
        }
    }

    companion object {

        val instance: ThreadBiz
            get() = App.appComponent.threadBiz
    }
}