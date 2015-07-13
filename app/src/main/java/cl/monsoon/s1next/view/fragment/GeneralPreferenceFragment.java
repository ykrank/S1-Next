package cl.monsoon.s1next.view.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.pref.GeneralPreferencesManager;
import cl.monsoon.s1next.data.pref.GeneralPreferencesRepository;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.event.FontSizeChangeEvent;
import cl.monsoon.s1next.event.ThemeChangeEvent;
import cl.monsoon.s1next.singleton.BusProvider;
import cl.monsoon.s1next.util.DeviceUtil;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.view.activity.SettingsActivity;

public final class GeneralPreferenceFragment extends BasePreferenceFragment implements Preference.OnPreferenceClickListener {

    private static final String PREF_KEY_DOWNLOADS = "pref_key_downloads";

    @Inject
    GeneralPreferencesManager mGeneralPreferencesManager;

    @Inject
    ThemeManager mThemeManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent(getActivity()).inject(this);

        addPreferencesFromResource(R.xml.general_preferences);

        findPreference(PREF_KEY_DOWNLOADS).setOnPreferenceClickListener(this);
        findPreference(GeneralPreferencesRepository.PREF_KEY_SIGNATURE).setSummary(
                DeviceUtil.getSignature());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case GeneralPreferencesRepository.PREF_KEY_THEME:
                mThemeManager.invalidateTheme();
                BusProvider.get().post(new ThemeChangeEvent());

                break;
            case GeneralPreferencesRepository.PREF_KEY_FONT_SIZE:
                mGeneralPreferencesManager.invalidateTextScale();
                ResourceUtil.setScaledDensity(getResources(),
                        mGeneralPreferencesManager.getTextScale());
                BusProvider.get().post(new FontSizeChangeEvent());

                break;
            case GeneralPreferencesRepository.PREF_KEY_SIGNATURE:
                mGeneralPreferencesManager.invalidateSignatureEnabled();

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
