package me.ykrank.s1next.data.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.db.dbmodel.DbThread;
import me.ykrank.s1next.data.db.dbmodel.DbThreadDao;

import static me.ykrank.s1next.data.db.dbmodel.DbThreadDao.Properties;

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

    private DbThreadDao getThreadDao() {
        return appDaoSessionManager.getDaoSession().getDbThreadDao();
    }

    @Nullable
    public DbThread getWithThreadId(int threadId) {
        return getThreadDao().queryBuilder()
                .where(Properties.ThreadId.eq(threadId))
                .unique();
    }

    public void saveThread(@NonNull DbThread dbThread) {
        DbThread oDbThread = getWithThreadId(dbThread.getThreadId());
        if (oDbThread == null) {
            getThreadDao().insert(dbThread);
        } else {
            oDbThread.copyFrom(dbThread);
            getThreadDao().update(oDbThread);
        }
    }

    public void delThread(int threadId) {
        DbThread oDbThread = getWithThreadId(threadId);
        if (oDbThread != null) {
            getThreadDao().delete(oDbThread);
        }
    }

}
