package me.ykrank.s1next.view.fragment.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v14.preference.PreferenceFragment;

import com.github.ykrank.androidtools.widget.track.DataTrackAgent;
import com.github.ykrank.androidtools.widget.track.event.page.LocalFragmentEndEvent;
import com.github.ykrank.androidtools.widget.track.event.page.LocalFragmentStartEvent;

import me.ykrank.s1next.App;

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
        trackAgent = App.Companion.get().getTrackAgent();
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
