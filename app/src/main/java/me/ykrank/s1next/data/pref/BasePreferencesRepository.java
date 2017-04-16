package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.BoolRes;
import android.support.annotation.StringRes;

/**
 * A base class wraps {@link SharedPreferences}.
 */
abstract class BasePreferencesRepository {

    final Context mContext;
    final SharedPreferences mSharedPreferences;

    BasePreferencesRepository(Context context, SharedPreferences sharedPreferences) {
        this.mContext = context.getApplicationContext();
        this.mSharedPreferences = sharedPreferences;
    }

    final String getPrefString(@StringRes int keyResId, @StringRes int defValueResId) {
        return getPrefString(keyResId, mContext.getString(defValueResId));
    }

    final String getPrefString(@StringRes int keyResId, String defValue) {
        return mSharedPreferences.getString(mContext.getString(keyResId), defValue);
    }

    final boolean getPrefBoolean(@StringRes int keyResId, @BoolRes int defValueResId) {
        return getPrefBoolean(keyResId, mContext.getResources().getBoolean(defValueResId));
    }

    final boolean getPrefBoolean(@StringRes int keyResId, boolean defValue) {
        return mSharedPreferences.getBoolean(mContext.getString(keyResId), defValue);
    }

    final void putPrefString(@StringRes int keyResId, String defValue) {
        mSharedPreferences.edit().putString(mContext.getString(keyResId), defValue).apply();
    }

    final void putPrefBoolean(@StringRes int keyResId, boolean defValue) {
        mSharedPreferences.edit().putBoolean(mContext.getString(keyResId), defValue).apply();
    }
}
