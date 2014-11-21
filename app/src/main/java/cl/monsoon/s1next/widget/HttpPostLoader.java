package cl.monsoon.s1next.widget;

import android.content.Context;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

import cl.monsoon.s1next.model.mapper.Deserialization;
import cl.monsoon.s1next.singleton.MyOkHttpClient;

/**
 * The bug in {@link HttpGetLoader} doesn't affect this,
 * because we didn't need to retain this post and start anther activity.
 * <p>
 * Use an HTTP POST to send a request body.
 */
public final class HttpPostLoader<D extends Deserialization> extends HttpGetLoader<D> {

    private RequestBody mRequestBody;

    public HttpPostLoader(Context context, String url, Class<D> clazz, RequestBody requestBody) {
        super(context, url, clazz);

        this.mRequestBody = requestBody;
    }

    /**
     * Use {@link cl.monsoon.s1next.widget.HttpPostLoader#onContentChanged(RequestBody)} instead.
     */
    @Override
    @Deprecated
    public void onContentChanged() {
        super.onContentChanged();
    }

    public void onContentChanged(RequestBody requestBody) {
        this.mRequestBody = requestBody;

        super.onContentChanged();
    }

    /**
     * Synchronous post but the {@link HttpPostLoader} is asynchronism.
     */
    @Override
    InputStream request() throws IOException {
        Request request = new Request.Builder()
                .url(mUrl)
                .post(mRequestBody)
                .build();

        mCall = MyOkHttpClient.get().newCall(request);

        Response response = mCall.execute();

        return response.body().byteStream();
    }
}
