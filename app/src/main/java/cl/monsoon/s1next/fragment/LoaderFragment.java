package cl.monsoon.s1next.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;

import com.squareup.okhttp.RequestBody;

import cl.monsoon.s1next.model.mapper.ResultWrapper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpGetLoader;
import cl.monsoon.s1next.widget.HttpPostLoader;

/**
 * Wrap {@link cl.monsoon.s1next.widget.HttpPostLoader} and ProgressDialog.
 */
public abstract class LoaderFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<AsyncResult<ResultWrapper>>, DialogInterface.OnCancelListener {

    /**
     * The serialization (saved instance state) Bundle key representing
     * whether is loading when configuration changed.
     */
    private static final String STATE_LOADING = "loading";

    /**
     * The serialization (saved instance state) Bundle key representing
     * current loader id.
     */
    private static final String STATE_ID_LOADER = "id_loader";

    private Loader mLoader;

    /**
     * Whether {@link android.content.Loader} is loading.
     */
    private Boolean mLoading = false;
    private int mLoaderId;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLoading = savedInstanceState.getBoolean(STATE_LOADING);
            mLoaderId = savedInstanceState.getInt(STATE_ID_LOADER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // show ProgressDialog if Loader is still loading (works when configuration changed)
        if (mLoading) {
            showProgressDialog();
            mLoader = getLoaderManager().initLoader(mLoaderId, null, this);
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
        outState.putInt(STATE_ID_LOADER, mLoaderId);
    }

    void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getProgressMessage());
            mProgressDialog.setOnCancelListener(this);
        }

        mProgressDialog.show();
    }

    protected abstract CharSequence getProgressMessage();

    @Override
    public void onCancel(DialogInterface dialog) {
        // cancel HTTP post
        // see HttpGetLoader#cancelLoad()
        if (mLoader instanceof HttpGetLoader) {
            //noinspection RedundantCast
            ((HttpGetLoader) mLoader).cancelLoad();
        } else {
            throw new ClassCastException(mLoader + " must extend HttpGetLoader.");
        }
        mLoading = false;
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    void startLoader(int loaderId) {
        mLoading = true;
        mLoaderId = loaderId;
        mLoader = getLoaderManager().getLoader(mLoaderId);
        if (mLoader == null) {
            mLoader = getLoaderManager().initLoader(mLoaderId, null, this);
        } else {
            if (mLoader instanceof HttpPostLoader) {
                // We need to change the post body
                // if we have Loader before.
                //noinspection RedundantCast
                ((HttpPostLoader) mLoader).onContentChanged(getRequestBody(mLoaderId));
            } else if (mLoader instanceof HttpGetLoader) {
                mLoader.onContentChanged();
            } else {
                throw new ClassCastException(mLoader + " must extend HttpGetLoader.");
            }
        }
    }

    abstract RequestBody getRequestBody(int loaderId);

    @Override
    public void onLoadFinished(Loader<AsyncResult<ResultWrapper>> loader, AsyncResult<ResultWrapper> data) {
        mLoading = false;
        dismissProgressDialog();
    }

    @Override
    public void onLoaderReset(Loader<AsyncResult<ResultWrapper>> loader) {

    }
}
