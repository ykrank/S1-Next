package me.ykrank.s1next.data.db;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.db.dbmodel.Thread;
import me.ykrank.s1next.data.db.dbmodel.ThreadDao;

import static me.ykrank.s1next.data.db.dbmodel.ReadProgressDao.Properties;

/**
 * 对帖子数据库的操作包装
 * Created by AdminYkrank on 2016/2/23.
 */
public class ThreadDbWrapper {
    private static ThreadDbWrapper dbWrapper = new ThreadDbWrapper();

    @Inject
    AppDaoSessionManager appDaoSessionManager;

    private ThreadDbWrapper() {
        App.getAppComponent().inject(this);
    }

    public static ThreadDbWrapper getInstance() {
        return dbWrapper;
    }

    private ThreadDao getThreadDao() {
        return appDaoSessionManager.getDaoSession().getThreadDao();
    }

    public Thread getWithThreadId(String threadId) {
        return getThreadDao().queryBuilder()
                .where(Properties.ThreadId.eq(threadId))
                .unique();
    }

    public void saveThread(@NonNull Thread thread) {
        Thread oThread = getWithThreadId(thread.getThreadId());
        if (oThread == null) {
            getThreadDao().insert(thread);
        } else {
            oThread.copyFrom(thread);
            getThreadDao().update(oThread);
        }
    }

    public void delThread(String threadId) {
        Thread oThread = getWithThreadId(threadId);
        if (oThread != null) {
            getThreadDao().delete(oThread);
        }
    }

}
