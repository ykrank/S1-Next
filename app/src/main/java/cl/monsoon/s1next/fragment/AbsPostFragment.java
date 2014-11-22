package cl.monsoon.s1next.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Loader;
import android.os.Bundle;

import com.squareup.okhttp.RequestBody;

import cl.monsoon.s1next.model.mapper.ResultWrapper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpPostLoader;

/**
 * Wrap {@link cl.monsoon.s1next.widget.HttpPostLoader} and ProgressDialog.
 */
public abstract class AbsPostFragment extends Fragment implements LoaderManager.LoaderCallbacks<AsyncResult<ResultWrapper>> {

    /**
     * The serialization (saved instance state) Bundle key representing
     * whether is loading when configuration changed.
     */
    private static final String STATE_LOADING = "loading";

    private static final int ID_LOADER = 0;

    private Loader mLoader;

    /**
     * Whether {@link android.content.Loader} is loading.
     */
    private Boolean mLoading = false;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLoading = savedInstanceState.getBoolean(STATE_LOADING);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // show ProgressDialog if Loader is still loading (works when configuration changed)
        if (mLoading) {
            showProgressDialog();
            mLoader = getLoaderManager().initLoader(ID_LOADER, null, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        dismissProgressDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_LOADING, mLoading);
    }

    void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getProgressMessage());
            mProgressDialog.setOnCancelListener(dialog -> {
                // see HttpGetLoader#cancelLoad()
                //noinspection RedundantCast
                ((HttpPostLoader) mLoader).cancelLoad();
                mLoading = false;
            });
        }

        mProgressDialog.show();
    }

    protected abstract CharSequence getProgressMessage();

    void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * @param requestBody used when content changed.
     */
    void startLoader(RequestBody requestBody) {
        mLoader = getLoaderManager().getLoader(ID_LOADER);
        if (mLoader == null) {
            mLoader = getLoaderManager().initLoader(ID_LOADER, null, this);
        } else {
            try {
                // pass RequestBody to change post body
                ((HttpPostLoader) mLoader).onContentChanged(requestBody);
            } catch (ClassCastException e) {
                throw new IllegalStateException(mLoader + " must extend HttpPostLoader.");
            }
        }
        mLoading = true;
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<ResultWrapper>> loader, AsyncResult<ResultWrapper> data) {
        mLoading = false;
        dismissProgressDialog();
    }

    @Override
    public void onLoaderReset(Loader<AsyncResult<ResultWrapper>> loader) {

    }
}
