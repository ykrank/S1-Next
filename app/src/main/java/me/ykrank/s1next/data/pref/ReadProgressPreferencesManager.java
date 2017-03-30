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

    private final Supplier<ReadProgress> mLastReadProgressSupplier = new Supplier<ReadProgress>() {

        @Override
        public ReadProgress get() {
            return mReadProgressPreferencesRepository.getLastReadProgress();
        }
    };

    private volatile Supplier<ReadProgress> mLastReadProgressMemorized = Suppliers.memoize(mLastReadProgressSupplier);

    public ReadProgressPreferencesManager(ReadProgressPreferencesRepository readProgressPreferencesRepository) {
        this.mReadProgressPreferencesRepository = readProgressPreferencesRepository;
    }

    public boolean isSaveAuto() {
        return mReadProgressPreferencesRepository.isSaveAuto();
    }

    public boolean isLoadAuto() {
        return mReadProgressPreferencesRepository.isLoadAuto();
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
