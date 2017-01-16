package me.ykrank.s1next.data.db;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import org.greenrobot.greendao.database.Database;

import me.ykrank.s1next.data.db.dbmodel.DaoMaster;
import me.ykrank.s1next.data.db.dbmodel.DaoSession;

/**
 * Created by ykrank on 2017/1/16.
 */

public final class AppDaoSessionManager {
    private Database database;
    private final Supplier<DaoSession> mDaoSessionSupplier = new Supplier<DaoSession>() {

        @Override
        public DaoSession get() {
            return new DaoMaster(database).newSession();
        }
    };

    private volatile Supplier<DaoSession> mDaoSessionMemorized = Suppliers.memoize(mDaoSessionSupplier);

    public AppDaoSessionManager(Database database) {
        this.database = database;
    }

    /**
     * Used for re get daoSession if database change.
     */
    public void reGetDaoSession() {
        mDaoSessionMemorized = Suppliers.memoize(mDaoSessionSupplier);
    }

    public float getFontScale() {
        return mFontScaleMemorized.get();
    }
}
