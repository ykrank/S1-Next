package me.ykrank.s1next.view.fragment.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v14.preference.PreferenceFragment;

import me.ykrank.s1next.App;
import me.ykrank.s1next.widget.track.DataTrackAgent;
import me.ykrank.s1next.widget.track.event.page.LocalFragmentEndEvent;
import me.ykrank.s1next.widget.track.event.page.LocalFragmentStartEvent;

/**
 * A helper class for registering/unregistering
 * {@link android.content.SharedPreferences.OnSharedPreferenceChangeListener}.
 */
public abstract class BasePreferenceFragment extends PreferenceFragment
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

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new LocalFragmentStartEvent(this));
    }

    @Override
    public void onPause() {
        trackAgent.post(new LocalFragmentEndEvent(this));
        super.onPause();
    }
}
