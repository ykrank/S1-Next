package cl.monsoon.s1next.data.pref;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

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

    private volatile Supplier<Boolean> mSaveAutoMemorized = Suppliers.memoize(mSaveAutoSupplier);
    private volatile Supplier<Boolean> mLoadAutoMemorized = Suppliers.memoize(mLoadAutoSupplier);

    public ReadProgressPreferencesManager(ReadProgressPreferencesRepository readProgressPreferencesRepository) {
        this.mReadProgressPreferencesRepository = readProgressPreferencesRepository;
    }

    public void invalidateSaveAuto(){
        mSaveAutoMemorized = Suppliers.memoize(mSaveAutoSupplier);
    }
    
    public boolean isSaveAuto(){
        return  mSaveAutoMemorized.get();
    }

    public void invalidateLoadAuto(){
        mLoadAutoMemorized = Suppliers.memoize(mLoadAutoSupplier);
    }
    
    public boolean isLoadAuto(){
        return  mLoadAutoMemorized.get();
    }
}
