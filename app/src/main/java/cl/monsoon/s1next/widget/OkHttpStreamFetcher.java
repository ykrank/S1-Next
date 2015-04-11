package cl.monsoon.s1next.widget;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.singleton.AvatarUrlCache;
import cl.monsoon.s1next.singleton.Settings;

import static com.squareup.okhttp.internal.http.StatusLine.HTTP_PERM_REDIRECT;
import static com.squareup.okhttp.internal.http.StatusLine.HTTP_TEMP_REDIRECT;
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
final class OkHttpStreamFetcher implements DataFetcher<InputStream> {

    private final OkHttpClient mOkHttpClient;
    private final String mUrl;

    private Call mCall;
    private ResponseBody mResponseBody;
    private InputStream mInputStream;

    public OkHttpStreamFetcher(OkHttpClient okHttpClient, GlideUrl glideUrl) {
        this.mOkHttpClient = okHttpClient;
        this.mUrl = glideUrl.toString();
    }

    /**
     * @see AvatarUrlCache
     */
    @Override
    public InputStream loadData(Priority priority) throws IOException {
        Key key = null;
        if (Api.isAvatarUrl(mUrl)) {
            key = new AvatarUrlCache.OriginalKey(
                    mUrl, Settings.Download.getAvatarCacheInvalidationIntervalSignature());
            if (AvatarUrlCache.has(key)) {
                throw new IOException("Already have cached this avatar (" + mUrl + ").");
            }
        }

        Request request = new Request.Builder()
                .url(mUrl)
                .build();

        mCall = mOkHttpClient.newCall(request);
        Response response = mCall.execute();
        mResponseBody = response.body();

        if (!response.isSuccessful()) {
            // if (this this a avatar URL) && (this URL is cacheable)
            if (key != null && isCacheable(response)) {
                AvatarUrlCache.put(key);
            }

            throw new IOException("Response (status code " + response.code() + ") is unsuccessful.");
        }

        mInputStream = mResponseBody.byteStream();
        return mInputStream;
    }

    /**
     * Forked form {@link com.squareup.okhttp.internal.http.CacheStrategy#isCacheable(Response, Request)}.
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
            case HTTP_PERM_REDIRECT:
                // These codes can be cached unless headers forbid it.
                break;

            case HTTP_MOVED_TEMP:
            case HTTP_TEMP_REDIRECT:
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

    @Override
    public void cleanup() {
        IOUtils.closeQuietly(mInputStream);
        IOUtils.closeQuietly(mResponseBody);
    }

    @Override
    public String getId() {
        return mUrl;
    }

    @Override
    public void cancel() {
        if (mCall != null) {
            mCall.cancel();
            mCall = null;
        }
    }
}
