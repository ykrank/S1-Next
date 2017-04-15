package me.ykrank.s1next.view.fragment.setting;

import android.content.SharedPreferences;
import android.os.Bundle;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.data.pref.PrefKey;
import me.ykrank.s1next.data.pref.ReadProgressPreferencesManager;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

public final class NetPreferenceFragment extends BasePreferenceFragment {
    public static final String TAG = NetPreferenceFragment.class.getName();

    private GeneralPreferencesManager mGeneralPreferencesManager;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preference_read_progress);
        mGeneralPreferencesManager = App.getAppComponent().getGeneralPreferencesManager();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PrefKey.PREF_KEY_READ_PROGRESS_SAVE_AUTO:
                break;
            case PrefKey.PREF_KEY_READ_PROGRESS_LOAD_AUTO:
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
