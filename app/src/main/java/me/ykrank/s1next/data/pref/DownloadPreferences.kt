package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences
import me.ykrank.s1next.R

/**
 * A helper class retrieving the download preferences from [SharedPreferences].
 */
class DownloadPreferencesImpl(context: Context, sharedPreferences: SharedPreferences) : BasePreferences(context, sharedPreferences), DownloadPreferences {

    override val totalImageCacheSizeIndex: Int
        get() = getPrefString(R.string.pref_key_image_total_cache_size,
                R.string.pref_image_total_cache_size_default_value).toInt()

    override val totalDataCacheSizeIndex: Int
        get() = getPrefString(R.string.pref_key_data_total_cache_size,
                R.string.pref_data_total_cache_size_default_value).toInt()

    override val avatarsDownloadStrategyIndex: Int
        get() = getPrefString(R.string.pref_key_download_avatars_strategy,
                R.string.pref_download_avatars_strategy_default_value).toInt()

    override val avatarResolutionStrategyIndex: Int
        get() = getPrefString(R.string.pref_key_avatar_resolution_strategy,
                R.string.pref_avatar_resolution_strategy_default_value).toInt()

    override val avatarCacheInvalidationInterval: Int
        get() = getPrefString(R.string.pref_key_avatar_cache_invalidation_interval,
                R.string.pref_avatar_cache_invalidation_interval_default_value).toInt()

    override val imagesDownloadStrategyIndex: Int
        get() = getPrefString(R.string.pref_key_download_images_strategy,
                R.string.pref_download_images_strategy_default_value).toInt()
}

interface DownloadPreferences {
    val totalImageCacheSizeIndex: Int
    val totalDataCacheSizeIndex: Int
    val avatarsDownloadStrategyIndex: Int
    val avatarResolutionStrategyIndex: Int
    val avatarCacheInvalidationInterval: Int
    val imagesDownloadStrategyIndex: Int
}