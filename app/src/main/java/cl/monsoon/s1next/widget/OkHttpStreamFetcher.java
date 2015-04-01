package cl.monsoon.s1next.widget;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.singleton.AvatarUrlCache;
import cl.monsoon.s1next.singleton.Setting;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_GONE;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MULT_CHOICE;
import static java.net.HttpURLConnection.HTTP_NOT_AUTHORITATIVE;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NOT_IMPLEMENTED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_PARTIAL;
import static java.net.HttpURLConnection.HTTP_REQ_TOO_LONG;


/**
 * Fetches an {@link java.io.InputStream} using the OkHttp library.
 * <p>
 * Forked from https://github.com/bumptech/glide/blob/master/integration/okhttp/src/main/java/com/bumptech/glide/integration/okhttp/OkHttpStreamFetcher.java
 */
final class OkHttpStreamFetcher implements DataFetcher<InputStream> {

    /**
     * According to RFC, we could cache some responses with following status codes.
     * <p>
     * See http://tools.ietf.org/html/rfc7231#section-6.1
     */
    private static final int[] CACHEABLE_RESPONSE_STATUS_CODES = new int[]{
            HTTP_OK,
            HTTP_NOT_AUTHORITATIVE,
            HTTP_NO_CONTENT,
            HTTP_PARTIAL,
            HTTP_MULT_CHOICE,
            HTTP_MOVED_PERM,
            HTTP_NOT_FOUND,
            HTTP_BAD_METHOD,
            HTTP_GONE,
            HTTP_REQ_TOO_LONG,
            HTTP_NOT_IMPLEMENTED
    };

    private final OkHttpClient mOkHttpClient;
    private final String mUrl;

    private Call mCall;
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
                    mUrl, Setting.Download.getAvatarCacheInvalidationIntervalSignature());
            if (AvatarUrlCache.has(key)) {
                throw new IOException("Already have cached this avatar (" + mUrl + ").");
            }
        }

        Request request = new Request.Builder()
                .url(mUrl)
                .build();

        mCall = mOkHttpClient.newCall(request);
        Response response = mCall.execute();

        if (!response.isSuccessful()) {
            response.body().close();

            // if (this this a avatar URL) && (this URL is cacheable)
            if (key != null && ArrayUtils.contains(
                    CACHEABLE_RESPONSE_STATUS_CODES, response.code())) {
                AvatarUrlCache.put(key);
            }

            throw new IOException("Response (status code " + response.code() + ") is unsuccessful.");
        }

        return response.body().byteStream();
    }

    @Override
    public void cleanup() {
        if (mInputStream == null) {
            return;
        }
        try {
            mInputStream.close();
            mInputStream = null;
        } catch (IOException ignored) {

        }
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
