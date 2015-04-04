package cl.monsoon.s1next.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.singleton.Settings;

public final class DownloadSettingsFragment extends BaseSettingsFragment {

    public static final String PREF_KEY_TOTAL_DOWNLOAD_CACHE_SIZE = "pref_key_download_total_cache_size";
    public static final String PREF_KEY_DOWNLOAD_AVATARS_STRATEGY = "pref_key_download_avatars_strategy";
    public static final String PREF_KEY_AVATAR_RESOLUTION_STRATEGY = "pref_key_avatar_resolution_strategy";
    public static final String PREF_KEY_AVATAR_CACHE_INVALIDATION_INTERVAL = "pref_key_avatar_cache_invalidation_interval";
    public static final String PREF_KEY_DOWNLOAD_IMAGES_STRATEGY = "pref_key_download_images_strategy";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.download_preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            // change avatar download strategy
            case PREF_KEY_DOWNLOAD_AVATARS_STRATEGY:
                Settings.Download.setAvatarsDownloadStrategy(sharedPreferences);

                break;
            // change avatar resolution strategy
            case PREF_KEY_AVATAR_RESOLUTION_STRATEGY:
                Settings.Download.setAvatarResolutionStrategy(sharedPreferences);

                break;
            // change avatar cache invalidation interval
            case PREF_KEY_AVATAR_CACHE_INVALIDATION_INTERVAL:
                Settings.Download.setAvatarCacheInvalidationInterval(sharedPreferences);

                break;
            // change images' download strategy
            case PREF_KEY_DOWNLOAD_IMAGES_STRATEGY:
                Settings.Download.setImagesDownloadStrategy(sharedPreferences);

                break;
        }
    }
}
