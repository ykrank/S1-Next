package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

public class DataPreferencesRepository extends BasePreferencesRepository {

    public DataPreferencesRepository(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    public boolean hasNewPm() {
        return mSharedPreferences.getBoolean(PrefKey.PREF_KEY_HAS_NEW_PM, false);
    }

    public void setHasNewPm(boolean hasNewPm) {
        mSharedPreferences.edit().putBoolean(PrefKey.PREF_KEY_HAS_NEW_PM, hasNewPm).apply();
    }

    public boolean hasNewNotice() {
        return mSharedPreferences.getBoolean(PrefKey.PREF_KEY_HAS_NEW_NOTICE, false);
    }

    public void setHasNewNotice(boolean hasNewNotice) {
        mSharedPreferences.edit().putBoolean(PrefKey.PREF_KEY_HAS_NEW_NOTICE, hasNewNotice).apply();
    }
}
