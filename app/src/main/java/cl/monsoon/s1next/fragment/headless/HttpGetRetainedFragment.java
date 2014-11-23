package cl.monsoon.s1next.fragment.headless;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.RemoteException;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

import cl.monsoon.s1next.model.mapper.Deserialization;
import cl.monsoon.s1next.singleton.MyObjectMapper;
import cl.monsoon.s1next.singleton.MyOkHttpClient;
import cl.monsoon.s1next.util.ObjectUtil;
import cl.monsoon.s1next.widget.AsyncResult;

/**
 * Load JSON from Internet and deserialize to data.
 * Also retain AsyncTask and data when configuration change.
 */
public class HttpGetRetainedFragment<D extends Deserialization> extends DataRetainedFragment<D> {

    public static final String TAG_PREFIX = "retained_fragment_";

    private AsyncHttpGetTask mAsyncHttpGetTask;

    private AsyncTaskCallback<D> mAsyncTaskCallback;

    /**
     * Attach to its host to get its {@link AsyncTaskCallback}.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // if its host is Activity
        // else its host is Fragment
        if (getTag() == null) {
            if (activity instanceof AsyncTaskCallback) {
                mAsyncTaskCallback = ObjectUtil.uncheckedCast(activity);
            } else {
                throw new ClassCastException(getActivity() + " must implement AsyncTaskCallback.");
            }
        } else {
            String hostFragmentTag = getTag().substring(TAG_PREFIX.length());
            Fragment fragment =
                    activity.getFragmentManager().findFragmentByTag(hostFragmentTag);

            if (fragment == null) {
                throw new IllegalStateException("Can't find Fragment which host this Fragment.");
            }

            if (fragment instanceof AsyncTaskCallback) {
                mAsyncTaskCallback = ObjectUtil.uncheckedCast(fragment);
            } else {
                throw new ClassCastException(fragment + " must implement AsyncTaskCallback.");
            }
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
     * Start {@link AsyncHttpGetTask} to load data.
     */
    public void execute(String url, Class<D> clazz) {
        if (!isRunning()) {
            mAsyncHttpGetTask =
                    (AsyncHttpGetTask) new AsyncHttpGetTask().params(url, clazz).execute();
        }
    }

    void cancel() {
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
     * A callback interface that all activities containing this Fragment must
     * implement.
     */
    public static interface AsyncTaskCallback<D extends Deserialization> {

        public void onPostExecute(AsyncResult<D> dAsyncResult);
    }

    class AsyncHttpGetTask extends AsyncTask<Void, Void, AsyncResult<D>> {

        String mUrl;
        Call mCall;

        /**
         * Used for JSON mapper.
         */
        private Class<D> mClass;

        @Override
        protected AsyncResult<D> doInBackground(Void... params) {
            AsyncResult<D> result = new AsyncResult<>();

            try {
                // get response body from Internet
                InputStream in = request();

                try {
                    // JSON mapper
                    result.data = MyObjectMapper.readValue(in, mClass);
                } catch (IOException e) {
                    throw new RemoteException(e.toString());
                }
            } catch (IOException | RemoteException e) {
                result.exception = e;
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
        InputStream request() throws IOException {
            Request request = new Request.Builder()
                    .url(mUrl)
                    .build();

            mCall = MyOkHttpClient.get().newCall(request);
            Response response = mCall.execute();
            mCall = null;

            return response.body().byteStream();
        }

        private void cancelRequest() {
            if (mCall != null) {
                mCall.cancel();
                mCall = null;
            }
        }

        AsyncHttpGetTask params(String url, Class<D> clazz) {
            mUrl = url;
            mClass = clazz;

            return this;
        }
    }
}
