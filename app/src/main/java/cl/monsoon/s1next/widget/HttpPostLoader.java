package cl.monsoon.s1next.widget;

import android.content.Context;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.io.InputStream;

import cl.monsoon.s1next.model.mapper.Deserializable;
import cl.monsoon.s1next.singleton.MyOkHttpClient;

/**
 * Uses an HTTP POST to send a request body.
 *
 * @see HttpGetLoader
 */
public final class HttpPostLoader<D extends Deserializable> extends HttpGetLoader<D> {

    private final RequestBody mRequestBody;

    public HttpPostLoader(Context context, String url, Class<D> clazz, RequestBody requestBody) {
        super(context, url, clazz);

        this.mRequestBody = requestBody;
    }

    /**
     * Synchronous HTTP POST but the {@link HttpPostLoader} is asynchronism.
     */
    @Override
    InputStream request() throws IOException {
        Request request = new Request.Builder()
                .url(mUrl)
                .post(mRequestBody)
                .build();

        mCall = MyOkHttpClient.getForNonIdempotent().newCall(request);
        Response response = mCall.execute();
        mCall = null;

        if (!response.isSuccessful()) {
            response.body().close();
            throw new HttpResponseException(response.code(), response.toString());
        }

        return response.body().byteStream();
    }
}
