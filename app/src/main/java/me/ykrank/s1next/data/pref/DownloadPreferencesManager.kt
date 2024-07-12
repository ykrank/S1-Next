package me.ykrank.s1next.data.pref

import androidx.annotation.IntDef
import com.bumptech.glide.load.Key
import com.bumptech.glide.signature.ObjectKey
import com.github.ykrank.androidtools.util.DateUtil
import me.ykrank.s1next.data.Wifi

/**
 * A manager manage the download preferences that are associated with settings.
 */
class DownloadPreferencesManager(
    private val mPreferencesProvider: DownloadPreferences,
    private val mWifi: Wifi
) {

    val totalImageCacheSize: Long
        get() = TotalDownloadCacheSize.getByte(mPreferencesProvider.totalImageCacheSizeIndex)

    val totalDataCacheSize: Int
        get() = TotalDownloadCacheSize.getMByte(mPreferencesProvider.totalDataCacheSizeIndex)

    var downloadPath: String?
        get() = mPreferencesProvider.downloadPath
        set(path) {
            mPreferencesProvider.downloadPath = path
        }

    val netCacheEnable: Boolean
        get() = mPreferencesProvider.netCacheEnable

    val isAvatarsDownload: Boolean
        get() = DownloadStrategyInternal.isDownload(
            mPreferencesProvider.avatarsDownloadStrategyIndex,
            mWifi.isWifiEnabled
        )

    val avatarCacheInvalidationIntervalSignature: Key
        get() = AvatarCacheInvalidationInterval.getSignature(mPreferencesProvider.avatarCacheInvalidationInterval)

    /**
     * Checks whether we need to download images.
     */
    val isImagesDownload: Boolean
        get() = DownloadStrategyInternal.isDownload(
            mPreferencesProvider.imagesDownloadStrategyIndex,
            mWifi.isWifiEnabled
        )

    val postMaxImageShow: Int
        get() = mPreferencesProvider.postMaxImageShow

    /**
     * Checks whether we need to monitor the Wi-Fi status.
     * We needn't monitor the Wi-Fi status if we needn't/should
     * download avatars or images.
     */
    fun needMonitorWifi(): Boolean {
        val avatarDownloadStrategy = mPreferencesProvider.avatarsDownloadStrategyIndex
        val imageDownloadStrategy = mPreferencesProvider.imagesDownloadStrategyIndex
        return avatarDownloadStrategy == DownloadStrategyInternal.WIFI || imageDownloadStrategy == DownloadStrategyInternal.WIFI
    }

    private object TotalDownloadCacheSize {
        private val LOW = 48
        private val NORMAL = 96
        private val HIGH = 192

        private val SIZE = intArrayOf(LOW, NORMAL, HIGH)

        fun getByte(index: Int): Long {
            return SIZE[index] * 1000L * 1000
        }

        fun getMByte(index: Int): Int {
            return if (index < 0 || index >= SIZE.size) SIZE[0] else SIZE[index]
        }
    }

    private object DownloadStrategyInternal {
        const val NOT = 0
        const val WIFI = 1
        const val ALWAYS = 2

        @IntDef(NOT, WIFI, ALWAYS)
        @Retention(AnnotationRetention.SOURCE)
        internal annotation class DownloadStrategy

        fun isDownload(@DownloadStrategy downloadStrategy: Int, hasWifi: Boolean): Boolean {
            return downloadStrategy == WIFI && hasWifi || downloadStrategy == ALWAYS
        }
    }

    private object AvatarResolutionStrategy {
        const val LOW = 0
        const val HIGH_WIFI = 1
        const val HIGH = 2

        @IntDef(LOW, HIGH_WIFI, HIGH)
        @Retention(AnnotationRetention.SOURCE)
        internal annotation class AvatarStrategy

        fun isHigherResolutionDownload(
            @AvatarStrategy avatarResolutionStrategy: Int,
            hasWifi: Boolean
        ): Boolean {
            return avatarResolutionStrategy == HIGH_WIFI && hasWifi || avatarResolutionStrategy == HIGH
        }
    }

    private object AvatarCacheInvalidationInterval {
        private val EVERY_DAY = DateUtil.today()
        private val EVERY_WEEK = DateUtil.dayOfWeek()
        private val EVERY_MONTH = DateUtil.dayOfMonth()

        private val VALUES = arrayOf(EVERY_DAY, EVERY_WEEK, EVERY_MONTH)

        /**
         * Gets a string signature in order to invalidate avatar every day/week/month.

         * @return A [Key] representing the string signature
         * * of date that will be mixed in to the cache key.
         */
        fun getSignature(index: Int): Key {
            return ObjectKey(if (index < 0 || index >= VALUES.size) VALUES[0] else VALUES[index])
        }
    }
}
