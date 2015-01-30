package cl.monsoon.s1next.fragment;

import android.content.SharedPreferences;
import android.preference.PreferenceFragment;

public abstract class BaseSettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
