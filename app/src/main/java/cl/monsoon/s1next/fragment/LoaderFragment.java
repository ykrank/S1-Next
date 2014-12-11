package cl.monsoon.s1next.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.IntDef;

import com.squareup.okhttp.RequestBody;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cl.monsoon.s1next.model.mapper.ResultWrapper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpGetLoader;
import cl.monsoon.s1next.widget.HttpPostLoader;

/**
 * Wrap {@link cl.monsoon.s1next.widget.HttpPostLoader} and ProgressDialog.
 */
public abstract class LoaderFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<AsyncResult<ResultWrapper>>, DialogInterface.OnCancelListener {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ID_LOADER_LOGIN, ID_LOADER_GET_AUTHENTICITY_TOKEN, ID_LOADER_POST_REPLY})
    public @interface LoaderId {

    }

    static final int ID_LOADER_LOGIN = 0;
    static final int ID_LOADER_GET_AUTHENTICITY_TOKEN = 1;
    static final int ID_LOADER_POST_REPLY = 2;

    /**
     * The serialization (saved instance state) Bundle key representing
     * whether is loading when configuration changed.
     */
    private static final String STATE_IS_LOADING = "is_loading";

    /**
     * The serialization (saved instance state) Bundle key representing
     * current loader id.
     */
    private static final String STATE_ID_LOADER = "id_loader";

    private Loader mLoader;

    /**
     * Whether {@link android.content.Loader} is loading.
     */
    private Boolean mIsLoading = false;
    private int mLoaderId;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mIsLoading = savedInstanceState.getBoolean(STATE_IS_LOADING);
            mLoaderId = savedInstanceState.getInt(STATE_ID_LOADER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // show ProgressDialog if Loader is still loading (works when configuration changed)
        if (mIsLoading) {
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

        outState.putBoolean(STATE_IS_LOADING, mIsLoading);
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
        mIsLoading = false;
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    void startLoader(@LoaderId int loaderId) {
        mIsLoading = true;
        mLoaderId = loaderId;
        mLoader = getLoaderManager().getLoader(mLoaderId);
        if (mLoader == null) {
            mLoader = getLoaderManager().initLoader(mLoaderId, null, this);
        } else {
            if (mLoader instanceof HttpPostLoader) {
                // We need to change the post body
                // if we have Loader before.
                ((HttpPostLoader) mLoader).onContentChanged(getRequestBody(mLoaderId));
            } else if (mLoader instanceof HttpGetLoader) {
                mLoader.onContentChanged();
            } else {
                throw new ClassCastException(mLoader + " must extend HttpGetLoader.");
            }
        }
    }

    abstract RequestBody getRequestBody(@LoaderId int loaderId);

    @Override
    public void onLoadFinished(Loader<AsyncResult<ResultWrapper>> loader, AsyncResult<ResultWrapper> data) {
        mIsLoading = false;
        dismissProgressDialog();
    }

    @Override
    public void onLoaderReset(Loader<AsyncResult<ResultWrapper>> loader) {

    }
}
