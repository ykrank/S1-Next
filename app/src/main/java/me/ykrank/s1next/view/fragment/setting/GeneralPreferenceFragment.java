package me.ykrank.s1next.view.fragment.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.event.FontSizeChangeEvent;
import me.ykrank.s1next.data.event.ThemeChangeEvent;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.data.pref.ThemeManager;
import me.ykrank.s1next.util.DeviceUtil;
import me.ykrank.s1next.util.ResourceUtil;
import me.ykrank.s1next.view.activity.SettingsActivity;
import me.ykrank.s1next.widget.EventBus;
import me.ykrank.s1next.widget.span.HtmlCompat;
import me.ykrank.s1next.widget.track.event.ThemeChangeTrackEvent;

import static me.ykrank.s1next.widget.span.HtmlCompat.FROM_HTML_MODE_LEGACY;

/**
 * An Activity includes general settings that allow users
 * to modify general features and behaviors such as theme
 * and font size.
 */
public final class GeneralPreferenceFragment extends BasePreferenceFragment
        implements Preference.OnPreferenceClickListener {

    @Inject
    EventBus mEventBus;

    @Inject
    GeneralPreferencesManager mGeneralPreferencesManager;

    @Inject
    ThemeManager mThemeManager;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preference_general);
        App.getAppComponent().inject(this);

        findPreference(getString(R.string.pref_key_downloads)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_blacklists)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_post_read_progress)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_backup)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_network)).setOnPreferenceClickListener(this);

        findPreference(getString(R.string.pref_key_signature)).setSummary(HtmlCompat.fromHtml(DeviceUtil.getSignature(getActivity()), FROM_HTML_MODE_LEGACY));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_theme))) {
            trackAgent.post(new ThemeChangeTrackEvent(false));
            mThemeManager.invalidateTheme();
            mEventBus.post(new ThemeChangeEvent());
        } else if (key.equals(getString(R.string.pref_key_font_size))) {
            mGeneralPreferencesManager.invalidateFontScale();
            // change scaling factor for fonts
            ResourceUtil.setScaledDensity(getActivity(),
                    mGeneralPreferencesManager.getFontScale());
            mEventBus.post(new FontSizeChangeEvent());
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key == null) {
            return false;
        }
        if (key.equals(getString(R.string.pref_key_downloads))) {
            SettingsActivity.startDownloadSettingsActivity(preference.getContext());
            return true;
        } else if (key.equals(getString(R.string.pref_key_blacklists))) {
            SettingsActivity.startBlackListSettingsActivity(preference.getContext());
            return true;
        } else if (key.equals(getString(R.string.pref_key_post_read_progress))) {
            SettingsActivity.startReadProgressSettingsActivity(preference.getContext());
            return true;
        } else if (key.equals(getString(R.string.pref_key_backup))) {
            SettingsActivity.startBackupSettingsActivity(preference.getContext());
            return true;
        } else if (key.equals(getString(R.string.pref_key_network))) {
            SettingsActivity.startNetworkSettingsActivity(preference.getContext());
            return true;
        }

        return false;
    }
}
