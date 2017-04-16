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
        return getPrefString(R.string.pref_key_download_total_cache_size,
                R.string.pref_download_total_cache_size_default_value);
    }

    public String getAvatarsDownloadStrategyString() {
        return getPrefString(R.string.pref_key_download_avatars_strategy,
                R.string.pref_download_avatars_strategy_default_value);
    }

    public String getAvatarResolutionStrategyString() {
        return getPrefString(R.string.pref_key_avatar_resolution_strategy,
                R.string.pref_avatar_resolution_strategy_default_value);
    }

    public String getAvatarCacheInvalidationIntervalString() {
        return getPrefString(R.string.pref_key_avatar_cache_invalidation_interval,
                R.string.pref_avatar_cache_invalidation_interval_default_value);
    }

    public String getImagesDownloadStrategyString() {
        return getPrefString(R.string.pref_key_download_images_strategy,
                R.string.pref_download_images_strategy_default_value);
    }
}
