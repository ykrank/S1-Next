package cl.monsoon.s1next.fragment.headless;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.v4.app.Fragment;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.io.InputStream;

import cl.monsoon.s1next.model.Extractable;
import cl.monsoon.s1next.singleton.MyObjectExtractor;
import cl.monsoon.s1next.singleton.MyOkHttpClient;
import cl.monsoon.s1next.util.ObjectUtil;
import cl.monsoon.s1next.widget.AsyncResult;

/**
 * Load data from the Internet and then extracted into POJO.
 * Also retain {@link HttpGetRetainedFragment.AsyncHttpGetTask}
 * and data when configuration change.
 */
public class HttpGetRetainedFragment<D extends Extractable> extends DataRetainedFragment<D> {

    public static final String TAG_PREFIX = "retained_fragment_";

    private AsyncHttpGetTask mAsyncHttpGetTask;

    private Callback<D> mAsyncTaskCallback;

    /**
     * Attach to its host to get its {@link Callback}.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // if its host is Activity
        // else its host is Fragment
        if (getTag() == null) {
            mAsyncTaskCallback =
                    ObjectUtil.uncheckedCast(ObjectUtil.cast(activity, Callback.class));
        } else {
            String hostFragmentTag = getTag().substring(TAG_PREFIX.length());
            Fragment fragment = getFragmentManager().findFragmentByTag(hostFragmentTag);

            if (fragment == null) {
                throw new IllegalStateException("Can't find Fragment which host this Fragment.");
            }

            mAsyncTaskCallback = ObjectUtil.uncheckedCast(ObjectUtil.cast(fragment, Callback.class));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mAsyncTaskCallback = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mAsyncHttpGetTask != null && mAsyncHttpGetTask.getStatus() == AsyncTask.Status.RUNNING) {
            cancel();
        }
    }

    /**
     * Execute {@link AsyncHttpGetTask} to load data.
     */
    public void execute(String url, Class<D> clazz) {
        if (!isRunning()) {
            mAsyncHttpGetTask =
                    (AsyncHttpGetTask) new AsyncHttpGetTask().params(url, clazz).execute();
        }
    }

    private void cancel() {
        if (isRunning()) {
            mAsyncHttpGetTask.cancelRequest();
            mAsyncHttpGetTask.cancel(true);
            mAsyncHttpGetTask = null;
        }
    }

    public boolean isRunning() {
        return mAsyncHttpGetTask != null && mAsyncHttpGetTask.getStatus() == AsyncTask.Status.RUNNING;
    }

    /**
     * A callback interface that all activities containing this Fragment must implement.
     */
    public static interface Callback<D extends Extractable> {

        public void onPostExecute(AsyncResult<D> dAsyncResult);
    }

    private class AsyncHttpGetTask extends AsyncTask<Void, Void, AsyncResult<D>> {

        private String mUrl;
        private Call mCall;

        /**
         * Used for JSON mapper.
         */
        private Class<D> mClass;

        @Override
        protected AsyncResult<D> doInBackground(Void... params) {
            AsyncResult<D> result = new AsyncResult<>();

            InputStream in = null;
            try {
                // get response body from Internet
                in = request();

                // JSON mapper
                result.data = MyObjectExtractor.readValue(in, mClass);
            } catch (IOException | RemoteException e) {
                result.exception = e;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {

                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(AsyncResult<D> dAsyncResult) {
            mAsyncTaskCallback.onPostExecute(dAsyncResult);
            setData(dAsyncResult.data);
        }

        /**
         * Synchronous get but the
         * {@link HttpGetRetainedFragment.AsyncHttpGetTask}
         * is asynchronism.
         */
        private InputStream request() throws IOException {
            Request request = new Request.Builder()
                    .url(mUrl)
                    .build();

            mCall = MyOkHttpClient.get().newCall(request);
            Response response = mCall.execute();
            mCall = null;

            if (!response.isSuccessful()) {
                response.body().close();
                throw new HttpResponseException(response.code(), response.toString());
            }

            return response.body().byteStream();
        }

        private void cancelRequest() {
            if (mCall != null) {
                mCall.cancel();
                mCall = null;
            }
        }

        private AsyncHttpGetTask params(String url, Class<D> clazz) {
            mUrl = url;
            mClass = clazz;

            return this;
        }
    }
}
