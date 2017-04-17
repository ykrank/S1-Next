package me.ykrank.s1next.data.db;

import android.database.Cursor;
import android.support.annotation.NonNull;

import io.reactivex.Single;
import me.ykrank.s1next.App;
import me.ykrank.s1next.data.db.dbmodel.History;
import me.ykrank.s1next.data.db.dbmodel.HistoryDao;

/**
 * 对历史数据库的操作包装
 * Created by AdminYkrank on 2016/2/23.
 */
public class HistoryDbWrapper {
    private final AppDaoSessionManager appDaoSessionManager;

    HistoryDbWrapper(AppDaoSessionManager appDaoSessionManager) {
        this.appDaoSessionManager = appDaoSessionManager;
    }

    public static HistoryDbWrapper getInstance() {
        return App.getDbComponent().getHistoryDbWrapper();
    }

    private HistoryDao getHistoryDao() {
        return appDaoSessionManager.getDaoSession().getHistoryDao();
    }

    public Single<Cursor> getBlackListCursor() {
        return Single.just(getHistoryDao().queryBuilder())
                .map(builder -> builder.buildCursor().query());
    }

    @NonNull
    public History fromCursor(@NonNull Cursor cursor) {
        return getHistoryDao().readEntity(cursor, 0);
    }
}
