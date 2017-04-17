package me.ykrank.s1next.view.fragment.setting;

import android.content.SharedPreferences;
import android.os.Bundle;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;

/**
 * An Activity includes download settings that allow users
 * to modify download features and behaviors such as cache
 * size and avatars/images download strategy.
 */
public final class DownloadPreferenceFragment extends BasePreferenceFragment {
    public static final String TAG = DownloadPreferenceFragment.class.getName();

    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        App.getPrefComponent().inject(this);
        addPreferencesFromResource(R.xml.preference_download);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_download_avatars_strategy))) {
            mDownloadPreferencesManager.invalidateAvatarsDownloadStrategy();
        } else if (key.equals(getString(R.string.pref_key_avatar_resolution_strategy))) {
            mDownloadPreferencesManager.invalidateAvatarsResolutionStrategy();
        } else if (key.equals(getString(R.string.pref_key_avatar_cache_invalidation_interval))) {
            mDownloadPreferencesManager.invalidateAvatarsCacheInvalidationInterval();
        } else if (key.equals(getString(R.string.pref_key_download_images_strategy))) {
            mDownloadPreferencesManager.invalidateImagesDownloadStrategy();
        }
    }
}
