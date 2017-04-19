package me.ykrank.s1next.data.db;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Single;
import me.ykrank.s1next.App;
import me.ykrank.s1next.data.db.dbmodel.History;
import me.ykrank.s1next.data.db.dbmodel.HistoryDao;

/**
 * 对历史数据库的操作包装
 * Created by AdminYkrank on 2016/2/23.
 */
public class HistoryDbWrapper {
    /**
     * max history count
     */
    private static final int MAX_SIZE = 50;

    private final AppDaoSessionManager appDaoSessionManager;

    HistoryDbWrapper(AppDaoSessionManager appDaoSessionManager) {
        this.appDaoSessionManager = appDaoSessionManager;
    }

    public static HistoryDbWrapper getInstance() {
        return App.getAppComponent().getHistoryDbWrapper();
    }

    private HistoryDao getHistoryDao() {
        return appDaoSessionManager.getDaoSession().getHistoryDao();
    }

    /**
     * limit {@link #MAX_SIZE} order by timestamp desc
     */
    public Single<Cursor> getHistoryListCursor() {
        return Single.just(getHistoryDao().queryBuilder().orderDesc(HistoryDao.Properties.Timestamp).limit(MAX_SIZE))
                .map(builder -> builder.buildCursor().query());
    }

    @NonNull
    public History fromCursor(@NonNull Cursor cursor) {
        return getHistoryDao().readEntity(cursor, 0);
    }

    /**
     * the last position of history order by timestamp desc, or null if count less than {@link #MAX_SIZE}
     */
    @Nullable
    public History getLastHistory() {
        return null;
    }

    /**
     * add new history
     */
    public void addNewHistory(History history) {
        History oldHistory = getHistoryDao().queryBuilder().where(HistoryDao.Properties.ThreadId.eq(history.getThreadId())).unique();
        if (oldHistory != null) {
            //have same threadId history
            getHistoryDao().update(history);
        } else {
            //the last history
            oldHistory = getLastHistory();
            if (oldHistory != null) {
                oldHistory.copyFrom(history);
                getHistoryDao().update(oldHistory);
            } else {
                getHistoryDao().insert(history);
            }
        }
    }
}
