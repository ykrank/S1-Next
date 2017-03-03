package me.ykrank.s1next.data.pref;


import android.support.annotation.Nullable;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import me.ykrank.s1next.data.db.dbmodel.ReadProgress;

/**
 * A manager manage the download preferences that are associated with settings.
 */
public final class ReadProgressPreferencesManager {

    private final ReadProgressPreferencesRepository mReadProgressPreferencesRepository;

    /**
     * Lazy Initialization.
     */
    private final Supplier<Boolean> mSaveAutoSupplier = new Supplier<Boolean>() {

        @Override
        public Boolean get() {
            return mReadProgressPreferencesRepository.isSaveAuto();
        }
    };

    private final Supplier<Boolean> mLoadAutoSupplier = new Supplier<Boolean>() {

        @Override
        public Boolean get() {
            return mReadProgressPreferencesRepository.isLoadAuto();
        }
    };

    private final Supplier<ReadProgress> mLastReadProgressSupplier = new Supplier<ReadProgress>() {

        @Override
        public ReadProgress get() {
            return mReadProgressPreferencesRepository.getLastReadProgress();
        }
    };

    private volatile Supplier<Boolean> mSaveAutoMemorized = Suppliers.memoize(mSaveAutoSupplier);
    private volatile Supplier<Boolean> mLoadAutoMemorized = Suppliers.memoize(mLoadAutoSupplier);
    private volatile Supplier<ReadProgress> mLastReadProgressMemorized = Suppliers.memoize(mLastReadProgressSupplier);

    public ReadProgressPreferencesManager(ReadProgressPreferencesRepository readProgressPreferencesRepository) {
        this.mReadProgressPreferencesRepository = readProgressPreferencesRepository;
    }

    public void invalidateSaveAuto() {
        mSaveAutoMemorized = Suppliers.memoize(mSaveAutoSupplier);
    }

    public boolean isSaveAuto() {
        return mSaveAutoMemorized.get();
    }

    public void invalidateLoadAuto() {
        mLoadAutoMemorized = Suppliers.memoize(mLoadAutoSupplier);
    }

    public boolean isLoadAuto() {
        return mLoadAutoMemorized.get();
    }

    @Nullable
    public ReadProgress getLastReadProgress() {
        return mLastReadProgressMemorized.get();
    }

    public void invalidateLastReadProgress() {
        mLastReadProgressMemorized = Suppliers.memoize(mLastReadProgressSupplier);
    }

    public boolean saveLastReadProgress(ReadProgress readProgress) {
        boolean bool = mReadProgressPreferencesRepository.saveLastReadProgress(readProgress);
        invalidateLastReadProgress();
        return bool;
    }
}
