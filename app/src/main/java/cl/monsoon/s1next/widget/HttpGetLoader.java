package cl.monsoon.s1next.widget;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Build;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

import cl.monsoon.s1next.model.Extractable;
import cl.monsoon.s1next.singleton.MyObjectExtractor;
import cl.monsoon.s1next.singleton.MyOkHttpClient;

/**
 * Pay attention to https://stackoverflow.com/questions/15897547/loader-unable-to-retain-itself-during-certain-configuration-change
 * We must not use this during certain configuration change.
 * <p>
 * Load data from the Internet and then extracted into POJO.
 * {@see android.content.AsyncTaskLoader}.
 *
 * @param <D> the data type which could be extracted into POJO.
 */
public class HttpGetLoader<D extends Extractable> extends AsyncTaskLoader<AsyncResult<D>> {

    final String mUrl;
    /**
     * Used for JSON mapper.
     */
    private final Class<D> mClass;
    /**
     * {@link Call} is the request that has been prepared for execution.
     */
    Call mCall;
    private AsyncResult<D> mAsyncResult;

    public HttpGetLoader(Context context, String url, Class<D> clazz) {
        super(context);

        this.mClass = clazz;
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if (mAsyncResult != null) {
            deliverResult(mAsyncResult);
        }

        if (takeContentChanged() || mAsyncResult == null) {
            // force an asynchronous load
            forceLoad();
        }
    }

    @Override
    public AsyncResult<D> loadInBackground() {
        AsyncResult<D> asyncResult = new AsyncResult<>();

        try {
            // get response body from Internet
            InputStream in = request();

            // JSON mapper
            asyncResult.data = MyObjectExtractor.readValue(in, mClass);
        } catch (IOException e) {
            asyncResult.exception = e;
        }

        return asyncResult;
    }

    @Override
    public void deliverResult(AsyncResult<D> result) {
        if (isReset()) {
            if (result != null) {
                onReleaseResources();
            }
        }

        mAsyncResult = result;

        if (isStarted()) {
            super.deliverResult(result);
        }
    }

    @Override
    public void onCanceled(AsyncResult<D> data) {
        onReleaseResources();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (mAsyncResult != null) {
            onReleaseResources();
            mAsyncResult = null;
        }
    }

    /**
     * {@link android.content.AsyncTaskLoader#cancelLoad} requires API 16,
     * so we override this methods to provide backport.
     */
    @Override
    public boolean cancelLoad() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return onCancelLoad();
        } else {
            return super.cancelLoad();
        }
    }

    /**
     * Synchronous get but the {@link HttpGetLoader} is asynchronism.
     */
    InputStream request() throws IOException {
        Request request = new Request.Builder()
                .url(mUrl)
                .build();

        mCall = MyOkHttpClient.get().newCall(request);

        Response response = mCall.execute();

        return response.body().byteStream();
    }

    /**
     * Cancel {@link Call} if possible.
     */
    void onReleaseResources() {
        if (mCall != null) {
            mCall.cancel();
            mCall = null;
        }
    }
}
