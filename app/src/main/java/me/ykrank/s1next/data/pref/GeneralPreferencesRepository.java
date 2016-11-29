package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.PostListFragment;
import me.ykrank.s1next.view.fragment.setting.GeneralPreferenceFragment;

/**
 * A helper class for retrieving the general preferences from {@link SharedPreferences}.
 */
public final class GeneralPreferencesRepository extends BasePreferencesRepository {

    public GeneralPreferencesRepository(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    public String getThemeString() {
        return getSharedPreferencesString(GeneralPreferenceFragment.PREF_KEY_THEME,
                R.string.pref_theme_default_value);
    }

    /**
     * Commits theme preference change for settings.
     *
     * @param key The new value for the theme preference.
     */
    public void applyThemeString(String key) {
        mSharedPreferences.edit().putString(GeneralPreferenceFragment.PREF_KEY_THEME, key).apply();
    }

    public String getFontSizeString() {
        return getSharedPreferencesString(GeneralPreferenceFragment.PREF_KEY_FONT_SIZE,
                R.string.pref_font_size_default_value);
    }

    public boolean isSignatureEnabled() {
        return mSharedPreferences.getBoolean(GeneralPreferenceFragment.PREF_KEY_SIGNATURE,
                mContext.getResources().getBoolean(R.bool.pref_signature_default_value));
    }

    public boolean isPostSelectable() {
        return mSharedPreferences.getBoolean(PostListFragment.PREF_KEY_POST_SELECTABLE,
                mContext.getResources().getBoolean(R.bool.pref_post_selectable_default_value));
    }

    public void setPostSelectable(boolean selectable) {
        mSharedPreferences.edit().putBoolean(PostListFragment.PREF_KEY_POST_SELECTABLE, selectable)
                .apply();
    }
}
