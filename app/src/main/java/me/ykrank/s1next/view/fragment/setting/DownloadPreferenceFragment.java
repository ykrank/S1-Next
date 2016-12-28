package me.ykrank.s1next.view.fragment.setting;

import android.content.SharedPreferences;
import android.os.Bundle;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.widget.track.event.PageEndEvent;
import me.ykrank.s1next.widget.track.event.PageStartEvent;

/**
 * An Activity includes download settings that allow users
 * to modify download features and behaviors such as cache
 * size and avatars/images download strategy.
 */
public final class DownloadPreferenceFragment extends BasePreferenceFragment {
    public static final String TAG = DownloadPreferenceFragment.class.getName();

    public static final String PREF_KEY_TOTAL_DOWNLOAD_CACHE_SIZE = "pref_key_download_total_cache_size";
    public static final String PREF_KEY_DOWNLOAD_AVATARS_STRATEGY = "pref_key_download_avatars_strategy";
    public static final String PREF_KEY_AVATAR_RESOLUTION_STRATEGY = "pref_key_avatar_resolution_strategy";
    public static final String PREF_KEY_AVATAR_CACHE_INVALIDATION_INTERVAL = "pref_key_avatar_cache_invalidation_interval";
    public static final String PREF_KEY_DOWNLOAD_IMAGES_STRATEGY = "pref_key_download_images_strategy";

    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        App.getPrefComponent(getActivity()).inject(this);
        addPreferencesFromResource(R.xml.preference_download);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PREF_KEY_DOWNLOAD_AVATARS_STRATEGY:
                mDownloadPreferencesManager.invalidateAvatarsDownloadStrategy();

                break;
            case PREF_KEY_AVATAR_RESOLUTION_STRATEGY:
                mDownloadPreferencesManager.invalidateAvatarsResolutionStrategy();

                break;
            case PREF_KEY_AVATAR_CACHE_INVALIDATION_INTERVAL:
                mDownloadPreferencesManager.invalidateAvatarsCacheInvalidationInterval();

                break;
            case PREF_KEY_DOWNLOAD_IMAGES_STRATEGY:
                mDownloadPreferencesManager.invalidateImagesDownloadStrategy();

                break;
            default:
                // fall through
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent("设置-下载-" + TAG));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent("设置-下载-" + TAG));
        super.onPause();
    }
}
