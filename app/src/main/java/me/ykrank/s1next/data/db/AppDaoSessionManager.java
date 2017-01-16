package me.ykrank.s1next.data.db;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import me.ykrank.s1next.data.db.dbmodel.DaoMaster;
import me.ykrank.s1next.data.db.dbmodel.DaoSession;

/**
 * Created by ykrank on 2017/1/16.
 */

public final class AppDaoSessionManager {
    private DaoMaster.OpenHelper daoHelper;
    private final Supplier<DaoSession> mDaoSessionSupplier = new Supplier<DaoSession>() {

        @Override
        public DaoSession get() {
            return new DaoMaster(daoHelper.getWritableDb()).newSession();
        }
    };

    private volatile Supplier<DaoSession> mDaoSessionMemorized = Suppliers.memoize(mDaoSessionSupplier);

    public AppDaoSessionManager(DaoMaster.OpenHelper daoHelper) {
        this.daoHelper = daoHelper;
    }

    /**
     * Used for re invalidate daoSession if database change.
     */
    public void invalidateDaoSession() {
        mDaoSessionMemorized = Suppliers.memoize(mDaoSessionSupplier);
    }

    public DaoSession getDaoSession() {
        return mDaoSessionMemorized.get();
    }

    /**
     * close db, then call {@link #getDaoSession()} will throw null exception.
     * you should call {@link #invalidateDaoSession()} before use db
     */
    public void closeDb() {
        daoHelper.close();
        mDaoSessionMemorized = null;
    }
}
