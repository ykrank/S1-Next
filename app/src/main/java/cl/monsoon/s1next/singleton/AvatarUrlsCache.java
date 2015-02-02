package cl.monsoon.s1next.singleton;


import android.util.Log;
import android.util.LruCache;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cl.monsoon.s1next.BuildConfig;
import cl.monsoon.s1next.MyApplication;

/**
 * According to RFC, we could cache some responses
 * with specific status codes (like 301, 404 and 405).
 * Glide would cache the images whose response is successful.
 * But we also cache those responses (actually are URLs
 * in implementation) whose status code is cacheable.
 * <p>
 * If this cache contains avatar URL that Glide is request,
 * just throw an exception to tell Glide to use the error
 * placeholder for this image.
 *
 * @see cl.monsoon.s1next.widget.OkHttpStreamFetcher#CACHEABLE_RESPONSE_STATUS_CODES
 * @see cl.monsoon.s1next.widget.OkHttpStreamFetcher#loadData(com.bumptech.glide.Priority)
 */
public enum AvatarUrlsCache {
    INSTANCE;

    private static final String DISK_CACHE_SUBDIRECTORY = "glide_urls_disk_cache";
    private static final int VALUE_COUNT = 1;

    /**
     * We only cache the avatar URLs as keys.
     * So we use this to represent the
     * null value because {@link android.util.LruCache}
     * doesn't accept null as a value.
     */
    private static final Object NULL_VALUE = new Object();

    private static final Object DISK_CACHE_LOCK = new Object();

    /**
     * We use both disk cache and memory cache.
     */
    private final DiskLruCache diskLruCache;
    private final LruCache<String, Object> lruCache;
    private final KeyGenerator keyGenerator;

    private AvatarUrlsCache() {
        lruCache = new LruCache<>(Config.AVATAR_URLS_MEMORY_CACHE_MAX_NUMBER);

        File file =
                new File(
                        MyApplication.getContext().getCacheDir().getPath()
                                + File.separator
                                + DISK_CACHE_SUBDIRECTORY);
        try {
            diskLruCache = DiskLruCache.open(
                    file,
                    BuildConfig.VERSION_CODE,
                    VALUE_COUNT,
                    Config.AVATAR_URLS_DISK_CACHE_MAX_SIZE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open the cache in " + file + ".", e);
        }

        keyGenerator = new KeyGenerator();
    }

    public static boolean has(Key key) {
        String encodedKey = INSTANCE.keyGenerator.getKey(key);
        if (encodedKey == null) {
            return false;
        }

        if (INSTANCE.lruCache.get(encodedKey) != null) {
            return true;
        }
        try {
            synchronized (DISK_CACHE_LOCK) {
                return INSTANCE.diskLruCache.get(encodedKey) != null;
            }
        } catch (IOException ignore) {
            return false;
        }
    }

    public static void put(Key key) {
        String encodedKey = INSTANCE.keyGenerator.getKey(key);
        if (encodedKey == null) {
            return;
        }

        INSTANCE.lruCache.put(encodedKey, NULL_VALUE);
        try {
            synchronized (DISK_CACHE_LOCK) {
                DiskLruCache.Editor editor = INSTANCE.diskLruCache.edit(encodedKey);
                if (editor.getFile(0).createNewFile()) {
                    editor.commit();
                } else {
                    editor.abort();
                }
            }
        } catch (IOException ignore) {

        }
    }

    /**
     * Forked from {@link com.bumptech.glide.load.engine.cache.SafeKeyGenerator}.
     */
    public static class KeyGenerator {

        private final String TAG = "KeyGenerator";

        private final LruCache<Key, String> lruCache =
                new LruCache<>(Config.AVATAR_URL_KEYS_MEMORY_CACHE_MAX_NUMBER);

        public String getKey(Key key) {
            String vale = lruCache.get(key);
            if (vale == null) {
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    key.updateDiskCacheKey(messageDigest);
                    vale = Util.sha256BytesToHex(messageDigest.digest());
                    lruCache.put(key, vale);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("No SHA-256 algorithm support.", e);
                } catch (UnsupportedEncodingException e) {
                    Log.w(TAG, "Unsupported encoding exception.", e);
                }
            }

            return vale;
        }
    }

    /**
     * Forked from {@link com.bumptech.glide.load.engine.OriginalKey}.
     */
    public static class OriginalKey implements Key {

        private final String id;
        private final Key signature;

        public OriginalKey(String id, Key signature) {
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
    }
}
