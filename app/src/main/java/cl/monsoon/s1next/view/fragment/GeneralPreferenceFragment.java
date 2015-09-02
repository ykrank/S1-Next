package cl.monsoon.s1next.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.event.FontSizeChangeEvent;
import cl.monsoon.s1next.data.event.ThemeChangeEvent;
import cl.monsoon.s1next.data.pref.GeneralPreferencesManager;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.util.DeviceUtil;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.view.activity.SettingsActivity;
import cl.monsoon.s1next.widget.EventBus;

/**
 * An Activity includes general settings that allow users
 * to modify general features and behaviors such as theme
 * and font size.
 */
public final class GeneralPreferenceFragment extends BasePreferenceFragment
        implements Preference.OnPreferenceClickListener {

    public static final String PREF_KEY_THEME = "pref_key_theme";
    public static final String PREF_KEY_FONT_SIZE = "pref_key_font_size_v2";
    public static final String PREF_KEY_SIGNATURE = "pref_key_signature";

    private static final String PREF_KEY_DOWNLOADS = "pref_key_downloads";

    @Inject
    EventBus mEventBus;

    @Inject
    GeneralPreferencesManager mGeneralPreferencesManager;

    @Inject
    ThemeManager mThemeManager;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preference_general);
        App.getAppComponent(getContext()).inject(this);

        findPreference(PREF_KEY_DOWNLOADS).setOnPreferenceClickListener(this);
        findPreference(PREF_KEY_SIGNATURE).setSummary(DeviceUtil.getSignature(getContext()));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PREF_KEY_THEME:
                mThemeManager.invalidateTheme();
                mEventBus.post(new ThemeChangeEvent());

                break;
            case PREF_KEY_FONT_SIZE:
                mGeneralPreferencesManager.invalidateFontScale();
                // change scaling factor for fonts
                ResourceUtil.setScaledDensity(getResources(),
                        mGeneralPreferencesManager.getFontScale());
                mEventBus.post(new FontSizeChangeEvent());

                break;
            case PREF_KEY_SIGNATURE:
                mGeneralPreferencesManager.invalidateSignatureEnabled();

                break;
            default:
                // fall through
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case PREF_KEY_DOWNLOADS:
                SettingsActivity.startDownloadSettingsActivity(preference.getContext());

                return true;
            default:
                // fall through
        }

        return false;
    }
}
