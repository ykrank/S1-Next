package cl.monsoon.s1next.view.fragment;

import android.content.SharedPreferences;
import android.support.annotation.CallSuper;
import android.support.v14.preference.PreferenceFragment;

/**
 * A helper class for registering/unregistering
 * {@link android.content.SharedPreferences.OnSharedPreferenceChangeListener}.
 */
abstract class BasePreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    @CallSuper
    public void onStop() {
        super.onStop();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
