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
import me.ykrank.s1next.data.pref.PrefKey;
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
        App.getPrefComponent().inject(this);

        findPreference(PrefKey.PREF_KEY_DOWNLOADS).setOnPreferenceClickListener(this);
        findPreference(PrefKey.PREF_KEY_BLACKLIST).setOnPreferenceClickListener(this);
        findPreference(PrefKey.PREF_KEY_READ_PROGRESS).setOnPreferenceClickListener(this);
        findPreference(PrefKey.PREF_KEY_BACKUP).setOnPreferenceClickListener(this);

        findPreference(PrefKey.PREF_KEY_SIGNATURE).setSummary(HtmlCompat.fromHtml(DeviceUtil.getSignature(getActivity()), FROM_HTML_MODE_LEGACY));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PrefKey.PREF_KEY_THEME:
                trackAgent.post(new ThemeChangeTrackEvent(false));
                mThemeManager.invalidateTheme();
                mEventBus.post(new ThemeChangeEvent());

                break;
            case PrefKey.PREF_KEY_FONT_SIZE:
                mGeneralPreferencesManager.invalidateFontScale();
                // change scaling factor for fonts
                ResourceUtil.setScaledDensity(getActivity(),
                        mGeneralPreferencesManager.getFontScale());
                mEventBus.post(new FontSizeChangeEvent());

                break;
            case PrefKey.PREF_KEY_SIGNATURE:
                break;
            default:
                // fall through
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case PrefKey.PREF_KEY_DOWNLOADS:
                SettingsActivity.startDownloadSettingsActivity(preference.getContext());
                return true;
            case PrefKey.PREF_KEY_BLACKLIST:
                SettingsActivity.startBlackListSettingsActivity(preference.getContext());
                return true;
            case PrefKey.PREF_KEY_READ_PROGRESS:
                SettingsActivity.startReadProgressSettingsActivity(preference.getContext());
                return true;
            case PrefKey.PREF_KEY_BACKUP:
                SettingsActivity.startBackupSettingsActivity(preference.getContext());
                return true;
            default:
                return false;
        }
    }
}
