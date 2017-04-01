package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;

/**
 * A helper class for retrieving the general preferences from {@link SharedPreferences}.
 */
public final class GeneralPreferencesRepository extends BasePreferencesRepository {

    public GeneralPreferencesRepository(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    public String getThemeString() {
        return getSharedPreferencesString(PrefKey.PREF_KEY_THEME,
                R.string.pref_theme_default_value);
    }

    /**
     * Commits theme preference change for settings.
     *
     * @param key The new value for the theme preference.
     */
    public void applyThemeString(String key) {
        mSharedPreferences.edit().putString(PrefKey.PREF_KEY_THEME, key).apply();
    }

    public String getFontSizeString() {
        return getSharedPreferencesString(PrefKey.PREF_KEY_FONT_SIZE,
                R.string.pref_font_size_default_value);
    }

    public boolean isSignatureEnabled() {
        return mSharedPreferences.getBoolean(PrefKey.PREF_KEY_SIGNATURE,
                mContext.getResources().getBoolean(R.bool.pref_signature_default_value));
    }

    public boolean isPostSelectable() {
        return mSharedPreferences.getBoolean(PrefKey.PREF_KEY_POST_SELECTABLE,
                mContext.getResources().getBoolean(R.bool.pref_post_selectable_default_value));
    }

    public void setPostSelectable(boolean selectable) {
        mSharedPreferences.edit().putBoolean(PrefKey.PREF_KEY_POST_SELECTABLE, selectable)
                .apply();
    }

    public boolean isQuickSideBarEnable() {
        return mSharedPreferences.getBoolean(PrefKey.PREF_KEY_QUICK_SIDE_BAR_ENABLE,
                mContext.getResources().getBoolean(R.bool.pref_quick_side_bar_enable_default_value));
    }

    public void setQuickSideBarEnable(boolean enable) {
        mSharedPreferences.edit().putBoolean(PrefKey.PREF_KEY_QUICK_SIDE_BAR_ENABLE, enable)
                .apply();
    }

    public String getBaseUrl() {
        return mSharedPreferences.getString(PrefKey.PREF_KEY_BASE_URL, Api.BASE_URL);
    }

    public void setBaseUrl(String baseUrl) {
        mSharedPreferences.edit().putString(PrefKey.PREF_KEY_BASE_URL, baseUrl)
                .apply();
    }

    public boolean isAutoCheckBaseUrl() {
        return mSharedPreferences.getBoolean(PrefKey.PREF_KEY_AUTO_CHECK_BASE_URL,
                mContext.getResources().getBoolean(R.bool.pref_key_auto_check_base_url_default_value));
    }

    public void setAutoCheckBaseUrl(boolean autoCheck) {
        mSharedPreferences.edit().putBoolean(PrefKey.PREF_KEY_AUTO_CHECK_BASE_URL, autoCheck)
                .apply();
    }
}
