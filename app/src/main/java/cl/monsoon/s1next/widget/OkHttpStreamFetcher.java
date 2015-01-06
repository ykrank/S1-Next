package cl.monsoon.s1next.widget;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.R;

/**
 * Fetches an {@link java.io.InputStream} using the OkHttp library.
 * <p>
 * Fork from https://github.com/bumptech/glide/blob/master/integration/okhttp/src/main/java/com/bumptech/glide/integration/okhttp/OkHttpStreamFetcher.java
 */
final class OkHttpStreamFetcher implements DataFetcher<InputStream> {

    // see http://tools.ietf.org/html/rfc7231#section-6.1
    // we need to cache the responses with following status codes
    private static final List<Integer> CACHEABLE_STATUS_CODES =
            Arrays.asList(
                    200, 203, 204, 206,
                    300, 301,
                    404, 405, 410, 414, 501);

    private final OkHttpClient mOkHttpClient;
    private final GlideUrl mGlideUrl;

    private Call mCall;
    private InputStream mInputStream;

    public OkHttpStreamFetcher(OkHttpClient okHttpClient, GlideUrl glideUrl) {
        this.mOkHttpClient = okHttpClient;
        this.mGlideUrl = glideUrl;
    }

    @Override
    public InputStream loadData(Priority priority) throws IOException {
        Request request = new Request.Builder()
                .url(mGlideUrl.toString())
                .build();

        mCall = mOkHttpClient.newCall(request);
        Response response = mCall.execute();
        mCall = null;

        // We need to provide InputStream (the default avatar placeholder)
        // in order to cache some responses whose status code is in CACHEABLE_STATUS_CODES.
        // But we needn't provide InputStream when response status code is in [200..300),
        // because we will get it (user avatar) from server.
        if (!response.isSuccessful()
                && CACHEABLE_STATUS_CODES.contains(response.code())) {
            response.body().close();

            //noinspection ResourceType
            mInputStream =
                    MyApplication.getContext()
                            .getResources().openRawResource(R.drawable.ic_avatar_placeholder);
        } else {
            mInputStream = response.body().byteStream();
        }

        return mInputStream;
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
        return mGlideUrl.toString();
    }

    @Override
    public void cancel() {
        if (mCall != null) {
            mCall.cancel();
            mCall = null;
        }
    }
}
