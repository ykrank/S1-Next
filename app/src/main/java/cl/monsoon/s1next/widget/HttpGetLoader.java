package cl.monsoon.s1next.widget;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import cl.monsoon.s1next.model.Extractable;
import cl.monsoon.s1next.singleton.ObjectExtractor;
import cl.monsoon.s1next.singleton.OkHttpClientProvider;
import cl.monsoon.s1next.util.ServerException;

/**
 * Loads data from the Internet and then extracted into POJO.
 * <p>
 * Pay attention to https://stackoverflow.com/questions/15897547/loader-unable-to-retain-itself-during-certain-configuration-change
 * We must retain data during certain configuration change.
 *
 * @param <D> the data type which could be extracted into POJO.
 * @see android.content.AsyncTaskLoader
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
    ResponseBody mResponseBody;

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

        InputStream inputStream = null;
        try {
            // get response body from Internet
            inputStream = request();

            // JSON mapper
            asyncResult.data = ObjectExtractor.extract(inputStream, mClass);
        } catch (IOException e) {
            asyncResult.exception = e;
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(mResponseBody);
        }

        return asyncResult;
    }

    @Override
    public void deliverResult(AsyncResult<D> result) {
        if (isReset() && result != null) {
            onReleaseResources();
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

        onReleaseResources();
        if (mAsyncResult != null) {
            mAsyncResult = null;
        }
    }

    /**
     * Synchronous HTTP GET but the {@link HttpGetLoader} is asynchronism.
     */
    InputStream request() throws IOException {
        Request request = new Request.Builder()
                .url(mUrl)
                .build();

        mCall = OkHttpClientProvider.get().newCall(request);
        Response response = mCall.execute();
        mResponseBody = response.body();

        if (!response.isSuccessful()) {
            throw new ServerException("Response (status code " + response.code() + ") is unsuccessful.");
        }

        return mResponseBody.byteStream();
    }

    /**
     * Cancels {@link Call} if possible.
     */
    private void onReleaseResources() {
        if (mCall != null) {
            mCall.cancel();
            mCall = null;
        }
    }
}
