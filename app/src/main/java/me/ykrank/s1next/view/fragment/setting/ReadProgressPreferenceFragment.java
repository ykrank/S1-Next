package me.ykrank.s1next.view.fragment.setting;

import android.content.SharedPreferences;
import android.os.Bundle;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.pref.ReadProgressPreferencesManager;

/**
 * An Activity includes download settings that allow users
 * to modify download features and behaviors such as cache
 * size and avatars/images download strategy.
 */
public final class ReadProgressPreferenceFragment extends BasePreferenceFragment {
    public static final String TAG = ReadProgressPreferenceFragment.class.getName();

    private ReadProgressPreferencesManager mReadProgressPreferencesManager;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preference_read_progress);
        mReadProgressPreferencesManager = App.getPrefComponent()
                .getReadProgressPreferencesManager();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // fall through
    }
}
