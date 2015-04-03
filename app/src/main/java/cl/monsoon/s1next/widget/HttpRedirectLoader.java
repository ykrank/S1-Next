package cl.monsoon.s1next.widget;

import android.content.Context;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

import cl.monsoon.s1next.model.Extractable;
import cl.monsoon.s1next.singleton.OkHttpClientManager;

/**
 * Capture the redirect URL for quote post link.
 *
 * @see HttpGetLoader
 */
public final class HttpRedirectLoader extends HttpGetLoader<HttpRedirectLoader.RedirectUrl> {

    public HttpRedirectLoader(Context context, String url) {
        super(context, url, RedirectUrl.class);
    }

    @Override
    public AsyncResult<RedirectUrl> loadInBackground() {
        AsyncResult<RedirectUrl> asyncResult = new AsyncResult<>();

        try {
            asyncResult.data = new RedirectUrl(getRedirectURL());
        } catch (IOException e) {
            asyncResult.exception = e;
        }

        return asyncResult;
    }

    public static class RedirectUrl implements Extractable {

        private final String url;

        public RedirectUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    @Override
    InputStream request() {
        throw new UnsupportedOperationException("This method is not supported in HttpRedirectLoader.");
    }

    private String getRedirectURL() throws IOException {
        Request request = new Request.Builder()
                .url(mUrl)
                .build();

        mCall = OkHttpClientManager.get().newCall(request);
        Response response = mCall.execute();
        response.body().close();

        return response.request().urlString();
    }
}
