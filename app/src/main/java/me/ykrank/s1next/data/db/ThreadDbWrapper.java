package me.ykrank.s1next.data.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.db.dbmodel.DbThread;
import me.ykrank.s1next.data.db.dbmodel.DbThreadDao;

import static me.ykrank.s1next.data.db.dbmodel.DbThreadDao.Properties;

/**
 * 对帖子数据库的操作包装
 * Created by AdminYkrank on 2016/2/23.
 */
public class ThreadDbWrapper {
    private final AppDaoSessionManager appDaoSessionManager;

    ThreadDbWrapper(AppDaoSessionManager appDaoSessionManager) {
        this.appDaoSessionManager = appDaoSessionManager;
    }

    public static ThreadDbWrapper getInstance() {
        return App.getDbComponent().getThreadDbWrapper();
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
