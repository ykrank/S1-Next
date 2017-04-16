package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

import me.ykrank.s1next.R;

public class DataPreferencesRepository extends BasePreferencesRepository {

    public DataPreferencesRepository(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    public boolean hasNewPm() {
        return getPrefBoolean(R.string.pref_key_has_new_pm, false);
    }

    public void setHasNewPm(boolean hasNewPm) {
        putPrefBoolean(R.string.pref_key_has_new_pm, hasNewPm);
    }

    public boolean hasNewNotice() {
        return getPrefBoolean(R.string.pref_key_has_new_notice, false);
    }

    public void setHasNewNotice(boolean hasNewNotice) {
        putPrefBoolean(R.string.pref_key_has_new_notice, hasNewNotice);
    }
}
