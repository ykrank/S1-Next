package me.ykrank.s1next.widget.glide;

import android.content.res.Resources;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import com.google.common.io.Closeables;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import me.ykrank.s1next.App;
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
            key = OriginalKey.Builder.getInstance().obtainAvatarKey(mDownloadPreferencesManager, url);
            if (AvatarUrlsCache.has(key)) {
                // already have cached this not success avatar url
                return null;
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
                return null;
            }

            throw new IOException("Response (status code " + response.code() + ") is unsuccessful.");
        } else {
            // if download success, and (this this a avatar URL) && (this URL is cacheable)
            // remove from cache list
            if (key != null && AvatarUrlsCache.has(key)) {
                AvatarUrlsCache.remove(key);
            }
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

}
