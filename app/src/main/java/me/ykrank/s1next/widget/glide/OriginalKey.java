package me.ykrank.s1next.widget.glide;

import android.util.LruCache;

import com.bumptech.glide.load.Key;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import me.ykrank.s1next.data.pref.DownloadPreferencesManager;

/**
 * Forked from {@link com.bumptech.glide.load.engine.OriginalKey}.
 */
public final class OriginalKey implements Key {

    private final String id;
    private final Key signature;

    private OriginalKey(String id, Key signature) {
        this.id = id;
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OriginalKey that = (OriginalKey) o;

        return id.equals(that.id) && signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + signature.hashCode();
        return result;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
        messageDigest.update(id.getBytes(STRING_CHARSET_NAME));
        signature.updateDiskCacheKey(messageDigest);
    }

    public static class Builder {
        private static final Builder instance = new Builder();

        private static final int ORIGIN_KEYS_MEMORY_CACHE_MAX_NUMBER = 1000;
        private final LruCache<String, OriginalKey> keyLruCache;

        private Builder() {
            keyLruCache = new LruCache<>(ORIGIN_KEYS_MEMORY_CACHE_MAX_NUMBER);
        }

        public static Builder getInstance() {
            return instance;
        }

        /**
         * obtain avatar cache key from download preference and url
         */
        public OriginalKey obtainAvatarKey(DownloadPreferencesManager manager, String url) {
            OriginalKey safeKey;
            synchronized (keyLruCache) {
                safeKey = keyLruCache.get(url);
            }
            if (safeKey == null) {
                safeKey = new OriginalKey(url, manager.getAvatarCacheInvalidationIntervalSignature());
                synchronized (keyLruCache) {
                    keyLruCache.put(url, safeKey);
                }
            }
            return safeKey;
        }
    }
}
