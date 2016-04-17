package cl.monsoon.s1next.view.fragment.setting;

import android.content.SharedPreferences;
import android.os.Bundle;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.pref.ReadProgressPreferencesManager;

/**
 * An Activity includes download settings that allow users
 * to modify download features and behaviors such as cache
 * size and avatars/images download strategy.
 */
public final class ReadProgressPreferenceFragment extends BasePreferenceFragment {
    public static final String TAG = ReadProgressPreferenceFragment.class.getName();

    public static final String PREF_KEY_READ_PROGRESS_SAVE_AUTO = "pref_key_read_progress_save_auto";
    public static final String PREF_KEY_READ_PROGRESS_LOAD_AUTO = "pref_key_read_progress_load_auto";

    private ReadProgressPreferencesManager mReadProgressPreferencesManager;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preference_read_progress);
        mReadProgressPreferencesManager = App.getAppComponent(getActivity())
                .getReadProgressPreferencesManager();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PREF_KEY_READ_PROGRESS_SAVE_AUTO:
                mReadProgressPreferencesManager.invalidateSaveAuto();
                break;
            case PREF_KEY_READ_PROGRESS_LOAD_AUTO:
                mReadProgressPreferencesManager.invalidateLoadAuto();
                break;
            default:
                // fall through
        }
    }
}
