package cl.monsoon.s1next.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.singleton.Config;

public final class DownloadSettingsFragment extends BaseSettingsFragment {

    public static final String TAG = "download_settings_fragment";

    public static final String PREF_DOWNLOAD_CACHE_SIZE = "pref_download_cache_size";
    public static final String PREF_KEY_DOWNLOAD_AVATARS = "pref_key_download_avatars";
    public static final String PREF_KEY_AVATAR_RESOLUTION = "pref_key_avatar_resolution";
    public static final String PREF_KEY_AVATAR_CACHE_INVALIDATION_INTERVAL = "pref_key_avatar_cache_invalidation_interval";
    public static final String PREF_KEY_DOWNLOAD_IMAGES = "pref_key_download_images";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.download_preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            // change avatar download strategy
            case PREF_KEY_DOWNLOAD_AVATARS:
                Config.setAvatarsDownloadStrategy(sharedPreferences);

                break;
            // change avatar resolution strategy
            case PREF_KEY_AVATAR_RESOLUTION:
                Config.setAvatarResolutionStrategy(sharedPreferences);

                break;
            // change avatar cache invalidation interval
            case PREF_KEY_AVATAR_CACHE_INVALIDATION_INTERVAL:
                Config.setAvatarCacheInvalidationInterval(sharedPreferences);

                break;
            // change images' download strategy
            case PREF_KEY_DOWNLOAD_IMAGES:
                Config.setImagesDownloadStrategy(sharedPreferences);

                break;
        }
    }
}
