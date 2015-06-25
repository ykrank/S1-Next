package cl.monsoon.s1next.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.activity.SettingsActivity;
import cl.monsoon.s1next.event.FontSizeChangeEvent;
import cl.monsoon.s1next.event.ThemeChangeEvent;
import cl.monsoon.s1next.singleton.BusProvider;
import cl.monsoon.s1next.singleton.Settings;
import cl.monsoon.s1next.util.DeviceUtil;
import cl.monsoon.s1next.util.ResourceUtil;

public final class MainPreferenceFragment extends BasePreferenceFragment implements Preference.OnPreferenceClickListener {

    public static final String PREF_KEY_THEME = "pref_key_theme";
    public static final String PREF_KEY_FONT_SIZE = "pref_key_font_size";
    public static final String PREF_KEY_SIGNATURE = "pref_key_signature";

    private static final String PREF_KEY_DOWNLOADS = "pref_key_downloads";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.main_preferences);

        findPreference(PREF_KEY_DOWNLOADS).setOnPreferenceClickListener(this);
        findPreference(PREF_KEY_SIGNATURE).setSummary(DeviceUtil.getSignature());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            // set current theme
            case PREF_KEY_THEME:
                Settings.Theme.setCurrentTheme(sharedPreferences);
                BusProvider.get().post(new ThemeChangeEvent());

                break;
            // change font size
            case PREF_KEY_FONT_SIZE:
                Settings.General.setTextScale(sharedPreferences);
                ResourceUtil.setScaledDensity(getResources(), Settings.General.getTextScale());
                BusProvider.get().post(new FontSizeChangeEvent());

                break;
            // enable/disable signature
            case PREF_KEY_SIGNATURE:
                Settings.General.setSignature(sharedPreferences);

                break;
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case PREF_KEY_DOWNLOADS:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra(SettingsActivity.ARG_SHOULD_SHOW_DOWNLOAD_SETTINGS, true);
                startActivity(intent);

                break;
        }

        return true;
    }
}
