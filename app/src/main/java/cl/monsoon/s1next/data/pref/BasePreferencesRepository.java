package cl.monsoon.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;

abstract class BasePreferencesRepository {

    final Context mContext;
    final SharedPreferences mSharedPreferences;

    BasePreferencesRepository(Context context, SharedPreferences sharedPreferences) {
        this.mContext = context;
        this.mSharedPreferences = sharedPreferences;
    }

    final String getSharedPreferencesString(String key, @StringRes int defStringResId) {
        return mSharedPreferences.getString(key, mContext.getString(defStringResId));
    }
}
