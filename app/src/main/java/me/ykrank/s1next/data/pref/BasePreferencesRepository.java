package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;

/**
 * A base class wraps {@link SharedPreferences}.
 */
abstract class BasePreferencesRepository {

    final Context mContext;
    final SharedPreferences mSharedPreferences;

    BasePreferencesRepository(Context context, SharedPreferences sharedPreferences) {
        this.mContext = context;
        this.mSharedPreferences = sharedPreferences;
    }

    /**
     * Retrieves a String value from the preferences.
     *
     * @param key            The name of the preference to retrieve.
     * @param defStringResId The resource id of the string which returns
     *                       if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue. Throws
     * ClassCastException if there is a preference with this name that is not
     * a String.
     * @throws ClassCastException
     */
    final String getSharedPreferencesString(String key, @StringRes int defStringResId) {
        return mSharedPreferences.getString(key, mContext.getString(defStringResId));
    }
}
