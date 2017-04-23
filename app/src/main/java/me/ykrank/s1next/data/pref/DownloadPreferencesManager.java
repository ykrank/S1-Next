package me.ykrank.s1next.data.pref;

import android.support.annotation.IntDef;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.StringSignature;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

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

    private final Supplier<AvatarResolutionStrategy> mAvatarResolutionStrategySupplier = new Supplier<AvatarResolutionStrategy>() {

        @Override
        public AvatarResolutionStrategy get() {
            return AvatarResolutionStrategy.VALUES[Integer.parseInt(
                    mDownloadPreferencesProvider.getAvatarResolutionStrategyString())];
        }
    };
    private final Supplier<AvatarCacheInvalidationInterval> mAvatarCacheInvalidationIntervalSupplier = new Supplier<AvatarCacheInvalidationInterval>() {

        @Override
        public AvatarCacheInvalidationInterval get() {
            return AvatarCacheInvalidationInterval.VALUES[Integer.parseInt(
                    mDownloadPreferencesProvider.getAvatarCacheInvalidationIntervalString())];
        }
    };

    private volatile Supplier<AvatarResolutionStrategy> mAvatarResolutionStrategyMemorized = Suppliers.memoize(mAvatarResolutionStrategySupplier);
    private volatile Supplier<AvatarCacheInvalidationInterval> mAvatarCacheInvalidationIntervalMemorized = Suppliers.memoize(mAvatarCacheInvalidationIntervalSupplier);

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

    /**
     * Used for invalidating the avatars' resolution strategy if settings change.
     */
    public void invalidateAvatarsResolutionStrategy() {
        mAvatarResolutionStrategyMemorized = Suppliers.memoize(mAvatarResolutionStrategySupplier);
    }

    public boolean isHighResolutionAvatarsDownload() {
        return mAvatarResolutionStrategyMemorized.get().isHigherResolutionDownload(
                mWifi.isWifiEnabled());
    }

    /**
     * Used for invalidating the avatars' cache invalidation interval preference
     * if settings change.
     */
    public void invalidateAvatarsCacheInvalidationInterval() {
        mAvatarCacheInvalidationIntervalMemorized = Suppliers.memoize(
                mAvatarCacheInvalidationIntervalSupplier);
    }

    public Key getAvatarCacheInvalidationIntervalSignature() {
        return mAvatarCacheInvalidationIntervalMemorized.get().getSignature();
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
            return SIZE[index];
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

    private enum AvatarResolutionStrategy {
        LOW, HIGH_WIFI, HIGH;

        private static final AvatarResolutionStrategy[] VALUES = AvatarResolutionStrategy.values();

        private boolean isHigherResolutionDownload(boolean hasWifi) {
            return equals(HIGH_WIFI) && hasWifi
                    || equals(HIGH);
        }
    }

    private enum AvatarCacheInvalidationInterval {
        EVERY_DAY(DateUtil::today),
        EVERY_WEEK(DateUtil::dayOfWeek),
        EVERY_MONTH(DateUtil::dayOfMonth);

        private static final AvatarCacheInvalidationInterval[] VALUES = AvatarCacheInvalidationInterval.values();

        private final Supplier<String> supplier;

        AvatarCacheInvalidationInterval(Supplier<String> supplier) {
            this.supplier = supplier;
        }

        /**
         * Gets a string signature in order to invalidate avatar every day/week/month.
         *
         * @return A {@link Key} representing the string signature
         * of date that will be mixed in to the cache key.
         */
        private Key getSignature() {
            return new StringSignature(supplier.get());
        }
    }
}
