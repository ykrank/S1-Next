package me.ykrank.s1next.widget.glide;

import android.util.LruCache;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import me.ykrank.s1next.App;
import me.ykrank.s1next.BuildConfig;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.util.L;

/**
 * We would get 404 status code if user hasn't set up their
 * avatar (in this case, we use the default avatar for this user).
 * So we can use this mechanism to cache these user avatars
 * (because they just use the default avatar).
 * <p>
 * According to RFC (http://tools.ietf.org/html/rfc7231#section-6.1),
 * we could cache some responses with specific status codes (like 301, 404 and 405).
 * Glide only cache the images whose response is successful.
 * But we also cache those user avatar URLs whose status code
 * is cacheable in order to tell Glide we use the default
 * avatar (error placeholder in implementation).
 * <p>
 * If this cache contains an avatar URL that Glide requests,
 * just throw an exception to tell Glide to use the error
 * placeholder for this image.
 *
 * @see AvatarStreamFetcher#loadData(com.bumptech.glide.Priority)
 */
public enum AvatarUrlsCache {
    INSTANCE;

    private static final int MEMORY_CACHE_MAX_NUMBER = 1000;
    private static final String DISK_CACHE_DIRECTORY = "avatar_urls_disk_cache";
    private static final long DISK_CACHE_MAX_SIZE = 1000 * 1000; // 1MB

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

    AvatarUrlsCache() {
        lruCache = new LruCache<>(MEMORY_CACHE_MAX_NUMBER);

        File file = new File(App.get().getCacheDir().getPath()
                + File.separator + DISK_CACHE_DIRECTORY);
        try {
            diskLruCache = DiskLruCache.open(file, BuildConfig.VERSION_CODE, 1,
                    DISK_CACHE_MAX_SIZE);
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
            L.report(ignore);
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
                // Editor will be null if there are two concurrent puts. In the worst case we will just silently fail.
                if (editor != null) {
                    try {
                        if (editor.getFile(0).createNewFile()) {
                            editor.commit();
                        }
                    } finally {
                        editor.abortUnlessCommitted();
                    }
                }
            }
        } catch (IOException ignore) {

        }
    }

    public static void remove(Key key) {
        String encodedKey = INSTANCE.keyGenerator.getKey(key);
        if (encodedKey == null) {
            return;
        }

        INSTANCE.lruCache.remove(encodedKey);
        try {
            synchronized (DISK_CACHE_LOCK) {
                INSTANCE.diskLruCache.remove(encodedKey);
            }
        } catch (IOException ignore) {

        }
    }

    public static void clearUserAvatarCache(String uid) {
        //clear avatar img error cache
        String smallAvatarUrl = Api.getAvatarSmallUrl(uid);
        String mediumAvatarUrl = Api.getAvatarMediumUrl(uid);
        String bigAvatarUrl = Api.getAvatarBigUrl(uid);
        DownloadPreferencesManager manager = App.getAppComponent()
                .getDownloadPreferencesManager();
        AvatarUrlsCache.remove(OriginalKey.Builder.getInstance().obtainAvatarKey(manager, smallAvatarUrl));
        AvatarUrlsCache.remove(OriginalKey.Builder.getInstance().obtainAvatarKey(manager, mediumAvatarUrl));
        AvatarUrlsCache.remove(OriginalKey.Builder.getInstance().obtainAvatarKey(manager, bigAvatarUrl));
    }

    /**
     * Forked from {@link com.bumptech.glide.load.engine.cache.SafeKeyGenerator}.
     */
    private static final class KeyGenerator {

        private static final int AVATAR_URL_KEYS_MEMORY_CACHE_MAX_NUMBER = 1000;

        private final LruCache<Key, String> lruCache = new LruCache<>(AVATAR_URL_KEYS_MEMORY_CACHE_MAX_NUMBER);

        public String getKey(Key key) {
            String safeKey;
            synchronized (lruCache) {
                safeKey = lruCache.get(key);
            }
            if (safeKey == null) {
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    key.updateDiskCacheKey(messageDigest);
                    safeKey = Util.sha256BytesToHex(messageDigest.digest());
                } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                synchronized (lruCache) {
                    lruCache.put(key, safeKey);
                }
            }
            return safeKey;
        }
    }
}
