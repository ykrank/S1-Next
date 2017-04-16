package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

import me.ykrank.s1next.R;

/**
 * A helper class for retrieving the general preferences from {@link SharedPreferences}.
 */
public final class GeneralPreferencesRepository extends BasePreferencesRepository {

    public GeneralPreferencesRepository(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    public String getThemeString() {
        return getPrefString(R.string.pref_key_theme, R.string.pref_theme_default_value);
    }

    /**
     * Commits theme preference change for settings.
     *
     * @param key The new value for the theme preference.
     */
    public void applyThemeString(String key) {
        putPrefString(R.string.pref_key_theme, key);
    }

    public String getFontSizeString() {
        return getPrefString(R.string.pref_key_font_size, R.string.pref_font_size_default_value);
    }

    public boolean isSignatureEnabled() {
        return getPrefBoolean(R.string.pref_key_signature, R.bool.pref_signature_default_value);
    }

    public boolean isPostSelectable() {
        return getPrefBoolean(R.string.pref_key_post_selectable, R.bool.pref_post_selectable_default_value);
    }

    public void setPostSelectable(boolean selectable) {
        putPrefBoolean(R.string.pref_key_post_selectable, selectable);
    }

    public boolean isQuickSideBarEnable() {
        return getPrefBoolean(R.string.pref_key_quick_side_bar_enable,
                R.bool.pref_quick_side_bar_enable_default_value);
    }

    public void setQuickSideBarEnable(boolean enable) {
        putPrefBoolean(R.string.pref_key_quick_side_bar_enable, enable);
    }
}
