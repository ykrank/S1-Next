package me.ykrank.s1next.data.pref;

public final class DataPreferencesManager {

    private final DataPreferencesRepository mPreferencesProvider;

    public DataPreferencesManager(DataPreferencesRepository mPreferencesProvider) {
        this.mPreferencesProvider = mPreferencesProvider;
    }

    public void invalidateHasNewPm(boolean hasNewPm) {
        mPreferencesProvider.setHasNewPm(hasNewPm);
    }

    public boolean hasNewPm() {
        return mPreferencesProvider.hasNewPm();
    }

    public void invalidateHasNewNotice(boolean hasNewNotice) {
        mPreferencesProvider.setHasNewNotice(hasNewNotice);
    }

    public boolean hasNewNotice() {
        return mPreferencesProvider.hasNewNotice();
    }
}
