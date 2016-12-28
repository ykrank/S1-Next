package me.ykrank.s1next.view.fragment.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v14.preference.PreferenceFragment;

import me.ykrank.s1next.App;
import me.ykrank.s1next.widget.track.DataTrackAgent;

/**
 * A helper class for registering/unregistering
 * {@link android.content.SharedPreferences.OnSharedPreferenceChangeListener}.
 */
abstract class BasePreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    DataTrackAgent trackAgent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trackAgent = App.get().getTrackAgent();
    }

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
