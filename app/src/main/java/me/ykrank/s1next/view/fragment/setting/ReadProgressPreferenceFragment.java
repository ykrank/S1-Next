package me.ykrank.s1next.view.fragment.setting;

import android.content.SharedPreferences;
import android.os.Bundle;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.pref.ReadProgressPreferencesManager;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

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
        mReadProgressPreferencesManager = App.getPrefComponent()
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

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getActivity(), "设置-浏览进度"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getActivity(), "设置-浏览进度"));
        super.onPause();
    }
}
