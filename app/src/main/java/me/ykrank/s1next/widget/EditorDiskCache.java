package me.ykrank.s1next.widget;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.LruCache;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.glide.util.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import me.ykrank.s1next.App;
import me.ykrank.s1next.BuildConfig;
import me.ykrank.s1next.util.L;

/**
 * use to cache document in editing
 * fork from {@link me.ykrank.s1next.widget.glide.AvatarUrlsCache}
 */
@WorkerThread
public class EditorDiskCache {
    private static final int MEMORY_CACHE_MAX_NUMBER = 4;
    private static final String DISK_CACHE_DIRECTORY = "editor_disk_cache";
    private static final long DISK_CACHE_MAX_SIZE = 100 * 1000; // 100KB

    private static final Object DISK_CACHE_LOCK = new Object();

    /**
     * We use both disk cache and memory cache.
     */
    private final DiskLruCache diskLruCache;
    private final LruCache<String, String> lruCache;
    private final KeyGenerator keyGenerator;

    public EditorDiskCache() {
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

    @Nullable
    public String get(@Nullable String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        String encodedKey = keyGenerator.getKey(key);

        String result;
        result = lruCache.get(encodedKey);
        if (result == null) {
            try {
                synchronized (DISK_CACHE_LOCK) {
                    DiskLruCache.Value value = diskLruCache.get(encodedKey);
                    if (value != null) {
                        result = value.getString(0);
                    }
                }
            } catch (IOException ignore) {
                result = null;
            }
        }

        return result;
    }

    /**
     * if value is empty, then remove cache
     */
    public void put(@Nullable String key, @Nullable String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }

        if (TextUtils.isEmpty(value)) {
            remove(key);
            return;
        }

        String encodedKey = keyGenerator.getKey(key);

        lruCache.put(encodedKey, value);
        try {
            synchronized (DISK_CACHE_LOCK) {
                DiskLruCache.Editor editor = diskLruCache.edit(encodedKey);
                // Editor will be null if there are two concurrent puts. In the worst case we will just silently fail.
                if (editor != null) {
                    BufferedWriter writer = null;
                    try {
                        File file = editor.getFile(0);
                        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
                        writer.write(value);
                        editor.commit();
                    } finally {
                        editor.abortUnlessCommitted();
                        if (writer != null) {
                            try {
                                writer.close();
                            } catch (IOException e) {
                                L.report(e);
                            }
                        }
                    }
                }
            }
        } catch (IOException ignore) {

        }
    }

    public void remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        String encodedKey = keyGenerator.getKey(key);

        lruCache.remove(encodedKey);
        try {
            synchronized (DISK_CACHE_LOCK) {
                diskLruCache.remove(encodedKey);
            }
        } catch (IOException ignore) {

        }
    }

    private static final class KeyGenerator {

        private static final int KEYS_MEMORY_CACHE_MAX_NUMBER = 1000;

        private final LruCache<String, String> lruCache = new LruCache<>(KEYS_MEMORY_CACHE_MAX_NUMBER);

        public String getKey(String value) {
            String safeKey;
            synchronized (lruCache) {
                safeKey = lruCache.get(value);
            }
            if (safeKey == null) {
                try {
                    Key key = new ObjectKey(value);
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    key.updateDiskCacheKey(messageDigest);
                    safeKey = Util.sha256BytesToHex(messageDigest.digest());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                synchronized (lruCache) {
                    lruCache.put(value, safeKey);
                }
            }
            return safeKey;
        }
    }
}
