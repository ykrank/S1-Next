package cl.monsoon.s1next.widget;

import android.content.Context;
import android.os.RemoteException;
import android.support.v4.content.AsyncTaskLoader;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.io.InputStream;

import cl.monsoon.s1next.model.Extractable;
import cl.monsoon.s1next.singleton.MyObjectExtractor;
import cl.monsoon.s1next.singleton.MyOkHttpClient;

/**
 * Loads data from the Internet and then extracted into POJO.
 * <p>
 * Pay attention to https://stackoverflow.com/questions/15897547/loader-unable-to-retain-itself-during-certain-configuration-change
 * We must not use this during certain configuration change.
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
            asyncResult.data = MyObjectExtractor.extract(inputStream, mClass);
        } catch (IOException | RemoteException e) {
            asyncResult.exception = e;
        } finally {
            IOUtils.closeQuietly(inputStream);
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

        if (mAsyncResult != null) {
            onReleaseResources();
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

        mCall = MyOkHttpClient.get().newCall(request);
        Response response = mCall.execute();

        if (!response.isSuccessful()) {
            response.body().close();
            throw new HttpResponseException(response.code(), response.toString());
        }

        return response.body().byteStream();
    }

    /**
     * Cancels {@link Call} if possible.
     */
    void onReleaseResources() {
        if (mCall != null) {
            mCall.cancel();
            mCall = null;
        }
    }
}
