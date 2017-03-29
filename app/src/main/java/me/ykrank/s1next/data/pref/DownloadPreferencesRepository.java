package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

import me.ykrank.s1next.R;

/**
 * A helper class retrieving the download preferences from {@link SharedPreferences}.
 */
public final class DownloadPreferencesRepository extends BasePreferencesRepository {

    public DownloadPreferencesRepository(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    public String getTotalDownloadCacheSizeString() {
        return getSharedPreferencesString(
                PrefKey.PREF_KEY_TOTAL_DOWNLOAD_CACHE_SIZE,
                R.string.pref_download_total_cache_size_default_value);
    }

    public String getAvatarsDownloadStrategyString() {
        return getSharedPreferencesString(
                PrefKey.PREF_KEY_DOWNLOAD_AVATARS_STRATEGY,
                R.string.pref_download_avatars_strategy_default_value);
    }

    public String getAvatarResolutionStrategyString() {
        return getSharedPreferencesString(
                PrefKey.PREF_KEY_AVATAR_RESOLUTION_STRATEGY,
                R.string.pref_avatar_resolution_strategy_default_value);
    }

    public String getAvatarCacheInvalidationIntervalString() {
        return getSharedPreferencesString(
                PrefKey.PREF_KEY_AVATAR_CACHE_INVALIDATION_INTERVAL,
                R.string.pref_avatar_cache_invalidation_interval_default_value);
    }

    public String getImagesDownloadStrategyString() {
        return getSharedPreferencesString(
                PrefKey.PREF_KEY_DOWNLOAD_IMAGES_STRATEGY,
                R.string.pref_download_images_strategy_default_value);
    }
}
