package cl.monsoon.s1next.data.pref;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.StringSignature;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.util.concurrent.Callable;

import cl.monsoon.s1next.data.Wifi;
import cl.monsoon.s1next.util.DateUtil;

/**
 * A manager manage the download preferences that are associated with settings.
 */
public final class DownloadPreferencesManager {

    private final DownloadPreferencesRepository mDownloadPreferencesProvider;
    private final Wifi mWifi;

    /**
     * Lazy Initialization.
     */
    private final Supplier<DownloadStrategy> mAvatarsDownloadStrategySupplier = new Supplier<DownloadStrategy>() {

        @Override
        public DownloadStrategy get() {
            return DownloadStrategy.VALUES[Integer.parseInt(
                    mDownloadPreferencesProvider.getAvatarsDownloadStrategyString())];
        }
    };
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
    private final Supplier<DownloadStrategy> mImagesDownloadStrategySupplier = new Supplier<DownloadStrategy>() {

        @Override
        public DownloadStrategy get() {
            return DownloadStrategy.VALUES[Integer.parseInt(
                    mDownloadPreferencesProvider.getImagesDownloadStrategyString())];
        }
    };

    private volatile Supplier<DownloadStrategy> mAvatarsDownloadStrategyMemorized = Suppliers.memoize(mAvatarsDownloadStrategySupplier);
    private volatile Supplier<AvatarResolutionStrategy> mAvatarResolutionStrategyMemorized = Suppliers.memoize(mAvatarResolutionStrategySupplier);
    private volatile Supplier<AvatarCacheInvalidationInterval> mAvatarCacheInvalidationIntervalMemorized = Suppliers.memoize(mAvatarCacheInvalidationIntervalSupplier);
    private volatile Supplier<DownloadStrategy> mImagesDownloadStrategyMemorized = Suppliers.memoize(mImagesDownloadStrategySupplier);

    public DownloadPreferencesManager(DownloadPreferencesRepository downloadPreferencesProvider, Wifi wifi) {
        this.mDownloadPreferencesProvider = downloadPreferencesProvider;
        this.mWifi = wifi;
    }

    public int getTotalDownloadCacheSize() {
        return TotalDownloadCacheSize.VALUES[Integer.parseInt(
                mDownloadPreferencesProvider.getTotalDownloadCacheSizeString())].size;
    }

    /**
     * Used for invalidating the avatars' download strategy if settings change.
     */
    public void invalidateAvatarsDownloadStrategy() {
        mAvatarsDownloadStrategyMemorized = Suppliers.memoize(mAvatarsDownloadStrategySupplier);
    }

    public boolean isAvatarsDownload() {
        return mAvatarsDownloadStrategyMemorized.get().isDownload(mWifi.isWifiEnabled());
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
     * Used for invalidating the images' download strategy if settings change.
     */
    public void invalidateImagesDownloadStrategy() {
        mImagesDownloadStrategyMemorized = Suppliers.memoize(mImagesDownloadStrategySupplier);
    }

    /**
     * Check whether we need to download images.
     */
    public boolean isImagesDownload() {
        return mImagesDownloadStrategyMemorized.get().isDownload(mWifi.isWifiEnabled());
    }

    /**
     * Check whether we should monitor the Wi-Fi status.
     * We needn't monitor the Wi-Fi status if we needn't/should download images.
     */
    public boolean shouldMonitorWifi() {
        return mAvatarsDownloadStrategyMemorized.get() != DownloadStrategy.NOT
                || mImagesDownloadStrategyMemorized.get() != DownloadStrategy.NOT;
    }

    private enum TotalDownloadCacheSize {
        // 32MB, 64MB, 128MB
        LOW(32), NORMAL(64), HIGH(128);

        private static final TotalDownloadCacheSize[] VALUES = TotalDownloadCacheSize.values();

        private final int size;

        TotalDownloadCacheSize(int size) {
            this.size = size * 1000 * 1000;
        }
    }

    private enum DownloadStrategy {
        NOT, WIFI, ALWAYS;

        private static final DownloadStrategy[] VALUES = DownloadStrategy.values();

        private boolean isDownload(boolean hasWifi) {
            return equals(WIFI) && hasWifi
                    || equals(ALWAYS);
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

        private final Callable<String> callable;

        AvatarCacheInvalidationInterval(Callable<String> callable) {
            this.callable = callable;
        }

        /**
         * Get a string signature in order to invalidate avatar every day/week/month.
         *
         * @return A {@link Key} representing the string signature
         * of date that will be mixed in to the cache key.
         */
        private Key getSignature() {
            try {
                return new StringSignature(callable.call());
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate a string signature.", e);
            }
        }
    }
}
