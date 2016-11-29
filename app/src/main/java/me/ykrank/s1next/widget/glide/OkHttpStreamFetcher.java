package me.ykrank.s1next.widget.glide;

import android.content.res.Resources;
import android.util.LruCache;

import com.bumptech.glide.Priority;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import com.bumptech.glide.util.Util;
import com.google.common.io.Closeables;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.BuildConfig;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.StatusLine;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_GONE;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_MULT_CHOICE;
import static java.net.HttpURLConnection.HTTP_NOT_AUTHORITATIVE;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NOT_IMPLEMENTED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_REQ_TOO_LONG;


/**
 * Fetches an {@link java.io.InputStream} using the OkHttp library.
 * <p>
 * Forked from https://github.com/bumptech/glide/blob/master/integration/okhttp/src/main/java/com/bumptech/glide/integration/okhttp/OkHttpStreamFetcher.java
 */
public final class OkHttpStreamFetcher implements DataFetcher<InputStream> {

    private final Resources mResources;
    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    private final OkHttpClient mOkHttpClient;
    private final GlideUrl mGlideUrl;

    private volatile Call mCall;
    private ResponseBody mResponseBody;
    private InputStream mInputStream;

    public OkHttpStreamFetcher(OkHttpClient okHttpClient, GlideUrl glideUrl) {
        this.mOkHttpClient = okHttpClient;
        this.mGlideUrl = glideUrl;

        mResources = App.get().getResources();
        App.getPrefComponent(App.get()).inject(this);
    }

    @Override
    public InputStream loadData(Priority priority) throws IOException {
        Key key = null;
        String url = mGlideUrl.toStringUrl();
        if (Api.isAvatarUrl(url)) {
            key = new OriginalKey(url,
                    mDownloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature());
            if (AvatarUrlsCache.has(key)) {
                // already have cached this avatar url
                mInputStream = mResources.openRawResource(+R.drawable.ic_avatar_placeholder);
                return mInputStream;
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        mCall = mOkHttpClient.newCall(request);
        Response response = mCall.execute();
        mResponseBody = response.body();

        if (!response.isSuccessful()) {
            // if (this this a avatar URL) && (this URL is cacheable)
            if (key != null && isCacheable(response)) {
                AvatarUrlsCache.put(key);
                mInputStream = mResources.openRawResource(+R.drawable.ic_avatar_placeholder);
                return mInputStream;
            }

            throw new IOException("Response (status code " + response.code() + ") is unsuccessful.");
        }

        long contentLength = mResponseBody.contentLength();
        mInputStream = ContentLengthInputStream.obtain(mResponseBody.byteStream(), contentLength);
        return mInputStream;
    }

    @Override
    public void cleanup() {
        Closeables.closeQuietly(mInputStream);
        try {
            Closeables.close(mResponseBody, true);
        } catch (IOException ignored) {

        }
    }

    @Override
    public String getId() {
        return mGlideUrl.getCacheKey();
    }

    @Override
    public void cancel() {
        if (mCall != null) {
            mCall.cancel();
            mCall = null;
        }
    }

    /**
     * Forked form {@link okhttp3.internal.cache.CacheStrategy#isCacheable(Response, Request)}.
     */
    private static boolean isCacheable(Response response) {
        // Always go to network for uncacheable response codes (RFC 7231 section 6.1),
        // This implementation doesn't support caching partial content.
        switch (response.code()) {
            case HTTP_OK:
            case HTTP_NOT_AUTHORITATIVE:
            case HTTP_NO_CONTENT:
            case HTTP_MULT_CHOICE:
            case HTTP_MOVED_PERM:
            case HTTP_NOT_FOUND:
            case HTTP_BAD_METHOD:
            case HTTP_GONE:
            case HTTP_REQ_TOO_LONG:
            case HTTP_NOT_IMPLEMENTED:
            case StatusLine.HTTP_PERM_REDIRECT:
                // These codes can be cached unless headers forbid it.
                break;

            case HTTP_MOVED_TEMP:
            case StatusLine.HTTP_TEMP_REDIRECT:
                // These codes can only be cached with the right response headers.
                // http://tools.ietf.org/html/rfc7234#section-3
                // s-maxage is not checked because OkHttp is a private cache that should ignore s-maxage.
                if (response.header("Expires") != null
                        || response.cacheControl().maxAgeSeconds() != -1
                        || response.cacheControl().isPublic()
                        || response.cacheControl().isPrivate()) {
                    break;
                }
                // Fall-through.

            default:
                // All other codes cannot be cached.
                return false;
        }

        return true;
    }

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
     * @see OkHttpStreamFetcher#loadData(com.bumptech.glide.Priority)
     */
    private enum AvatarUrlsCache {
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

        private static boolean has(Key key) {
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

        private static void put(Key key) {
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

    /**
     * Forked from {@link com.bumptech.glide.load.engine.OriginalKey}.
     */
    private static final class OriginalKey implements Key {

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
