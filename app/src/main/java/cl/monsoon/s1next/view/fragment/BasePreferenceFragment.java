package cl.monsoon.s1next.view.fragment;

import android.content.SharedPreferences;
import android.preference.PreferenceFragment;

/**
 * A helper class for registering/unregistering
 * {@link android.content.SharedPreferences.OnSharedPreferenceChangeListener}.
 */
public abstract class BasePreferenceFragment extends PreferenceFragment
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
