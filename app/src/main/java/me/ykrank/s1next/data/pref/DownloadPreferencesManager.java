package me.ykrank.s1next.data.pref;

import android.support.annotation.IntDef;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.ObjectKey;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.ykrank.s1next.data.Wifi;
import me.ykrank.s1next.util.DateUtil;

/**
 * A manager manage the download preferences that are associated with settings.
 */
public final class DownloadPreferencesManager {

    private final DownloadPreferencesRepository mDownloadPreferencesProvider;
    private final Wifi mWifi;

    public DownloadPreferencesManager(DownloadPreferencesRepository downloadPreferencesProvider, Wifi wifi) {
        this.mDownloadPreferencesProvider = downloadPreferencesProvider;
        this.mWifi = wifi;
    }

    public int getTotalImageCacheSize() {
        return TotalDownloadCacheSize.getByte(Integer.parseInt(
                mDownloadPreferencesProvider.getTotalImageCacheSizeString()));
    }

    public int getTotalDataCacheSize() {
        return TotalDownloadCacheSize.getMByte(Integer.parseInt(
                mDownloadPreferencesProvider.getTotalDataCacheSizeString()));
    }

    public boolean isAvatarsDownload() {
        int avatarDownloadStrategy = Integer.parseInt(mDownloadPreferencesProvider.getAvatarsDownloadStrategyString());
        return DownloadStrategyInternal.isDownload(avatarDownloadStrategy, mWifi.isWifiEnabled());
    }

    public boolean isHighResolutionAvatarsDownload() {
        int avatarResolutionStrategy = Integer.parseInt(mDownloadPreferencesProvider.getAvatarResolutionStrategyString());
        return AvatarResolutionStrategy.isHigherResolutionDownload(avatarResolutionStrategy, mWifi.isWifiEnabled());
    }

    public Key getAvatarCacheInvalidationIntervalSignature() {
        int interval = Integer.parseInt(mDownloadPreferencesProvider.getAvatarCacheInvalidationIntervalString());
        return AvatarCacheInvalidationInterval.getSignature(interval);
    }

    /**
     * Checks whether we need to download images.
     */
    public boolean isImagesDownload() {
        int imageDownloadStrategy = Integer.parseInt(mDownloadPreferencesProvider.getImagesDownloadStrategyString());
        return DownloadStrategyInternal.isDownload(imageDownloadStrategy, mWifi.isWifiEnabled());
    }

    /**
     * Checks whether we need to monitor the Wi-Fi status.
     * We needn't monitor the Wi-Fi status if we needn't/should
     * download avatars or images.
     */
    public boolean needMonitorWifi() {
        int avatarDownloadStrategy = Integer.parseInt(mDownloadPreferencesProvider.getAvatarsDownloadStrategyString());
        int imageDownloadStrategy = Integer.parseInt(mDownloadPreferencesProvider.getImagesDownloadStrategyString());
        return avatarDownloadStrategy == DownloadStrategyInternal.WIFI
                || imageDownloadStrategy == DownloadStrategyInternal.WIFI;
    }

    private static class TotalDownloadCacheSize {
        private static final int LOW = 32;
        private static final int NORMAL = 64;
        private static final int HIGH = 128;

        private static final int[] SIZE = new int[]{LOW, NORMAL, HIGH};

        public static int getByte(int index) {
            return SIZE[index] * 1000 * 1000;
        }

        public static int getMByte(int index) {
            return index < 0 || index >= SIZE.length ? SIZE[0] : SIZE[index];
        }
    }

    private static class DownloadStrategyInternal {
        private static final int NOT = 0;
        private static final int WIFI = 1;
        private static final int ALWAYS = 2;

        @IntDef({NOT, WIFI, ALWAYS})
        @Retention(RetentionPolicy.SOURCE)
        @interface DownloadStrategy {
        }

        private static boolean isDownload(int downloadStrategy, boolean hasWifi) {
            return downloadStrategy == WIFI && hasWifi
                    || downloadStrategy == ALWAYS;
        }
    }

    private static class AvatarResolutionStrategy {
        private static final int LOW = 0;
        private static final int HIGH_WIFI = 1;
        private static final int HIGH = 2;

        private static boolean isHigherResolutionDownload(int avatarResolutionStrategy, boolean hasWifi) {
            return avatarResolutionStrategy == HIGH_WIFI && hasWifi
                    || avatarResolutionStrategy == HIGH;
        }
    }

    private static class AvatarCacheInvalidationInterval {
        private static final String EVERY_DAY = DateUtil.today();
        private static final String EVERY_WEEK = DateUtil.dayOfWeek();
        private static final String EVERY_MONTH = DateUtil.dayOfMonth();

        private static final String[] VALUES = new String[]{EVERY_DAY, EVERY_WEEK, EVERY_MONTH};

        /**
         * Gets a string signature in order to invalidate avatar every day/week/month.
         *
         * @return A {@link Key} representing the string signature
         * of date that will be mixed in to the cache key.
         */
        private static Key getSignature(int index) {
            return new ObjectKey(index < 0 || index >= VALUES.length ? VALUES[0] : VALUES[index]);
        }
    }
}
