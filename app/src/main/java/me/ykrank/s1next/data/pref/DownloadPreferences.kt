package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.github.ykrank.androidtools.data.BasePreferences
import com.github.ykrank.androidtools.data.PreferenceDelegates
import me.ykrank.s1next.R

/**
 * A helper class retrieving the download preferences from [SharedPreferences].
 */
class DownloadPreferencesImpl(context: Context, sharedPreferences: SharedPreferences) :
    BasePreferences(context, sharedPreferences), DownloadPreferences {

    override val totalImageCacheSizeIndex: Int by PreferenceDelegates.int(
        R.string.pref_key_image_total_cache_size, R.string.pref_image_total_cache_size_default_value
    )

    override val totalDataCacheSizeIndex: Int by PreferenceDelegates.int(
        R.string.pref_key_data_total_cache_size, R.string.pref_data_total_cache_size_default_value
    )

    override val totalDataCacheSize: Int
        get() = mContext.resources.getStringArray(R.array.pref_data_total_cache_size_entries)[totalDataCacheSizeIndex].toInt()

    override var downloadPath: String? by PreferenceDelegates.string(
        R.string.pref_key_data_download_path, null
    )

    override val netCacheEnable: Boolean by PreferenceDelegates.bool(
        R.string.pref_key_net_cache_enable, R.bool.pref_net_cache_default_value
    )

    override val avatarsDownloadStrategyIndex: Int by PreferenceDelegates.int(
        R.string.pref_key_download_avatars_strategy,
        R.string.pref_download_avatars_strategy_default_value
    )

    override val avatarCacheInvalidationInterval: Int by PreferenceDelegates.int(
        R.string.pref_key_avatar_cache_invalidation_interval,
        R.string.pref_avatar_cache_invalidation_interval_default_value
    )

    override val imagesDownloadStrategyIndex: Int by PreferenceDelegates.int(
        R.string.pref_key_download_images_strategy,
        R.string.pref_download_images_strategy_default_value
    )

    override val postMaxImageShow: Int by PreferenceDelegates.int(
        R.string.pref_key_post_max_image_show, R.string.pref_post_max_image_show_default_value
    )
}

interface DownloadPreferences {
    val totalImageCacheSizeIndex: Int
    val totalDataCacheSizeIndex: Int
    val totalDataCacheSize: Int
    var downloadPath: String?
    val netCacheEnable: Boolean
    val avatarsDownloadStrategyIndex: Int
    val avatarCacheInvalidationInterval: Int
    val imagesDownloadStrategyIndex: Int
    val postMaxImageShow: Int
}