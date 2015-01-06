package cl.monsoon.s1next.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.squareup.okhttp.RequestBody;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cl.monsoon.s1next.model.Extractable;
import cl.monsoon.s1next.widget.AsyncResult;

/**
 * Wrap {@link cl.monsoon.s1next.widget.HttpPostLoader}and {@link android.support.v4.app.DialogFragment}.
 */
public abstract class LoaderDialogFragment<D extends Extractable>
        extends DialogFragment
        implements LoaderManager.LoaderCallbacks<AsyncResult<D>> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ID_LOADER_LOGIN,
            ID_LOADER_GET_AUTHENTICITY_TOKEN,
            ID_LOADER_ADD_THREAD_TO_FAVOURITES,
            ID_LOADER_POST_REPLY,
            ID_LOADER_GET_QUOTE_EXTRA_INFO, ID_LOADER_POST_QUOTE})
    private @interface LoaderId {

    }

    static final int ID_LOADER_LOGIN = 0;
    protected static final int ID_LOADER_GET_AUTHENTICITY_TOKEN = 1;
    protected static final int ID_LOADER_ADD_THREAD_TO_FAVOURITES = 2;
    static final int ID_LOADER_POST_REPLY = 3;
    static final int ID_LOADER_GET_QUOTE_EXTRA_INFO = 4;
    static final int ID_LOADER_POST_QUOTE = 5;

    /**
     * The serialization (saved instance state) Bundle key representing
     * current loader id.
     */
    private static final String STATE_ID_LOADER = "id_loader";

    private int mLoaderId = -1;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mLoaderId = savedInstanceState.getInt(STATE_ID_LOADER);
        }

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getProgressMessage());

        return progressDialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        int loaderId =
                mLoaderId != -1
                        ? mLoaderId
                        : getStartLoaderId();
        getLoaderManager().initLoader(loaderId, null, this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_ID_LOADER, mLoaderId);
    }

    protected abstract CharSequence getProgressMessage();

    @LoaderId
    protected abstract int getStartLoaderId();

    protected RequestBody getRequestBody(@LoaderId int loaderId) {
        throw new IllegalStateException("Loader ID can't be " + loaderId + ".");
    }

    @Override
    public Loader<AsyncResult<D>> onCreateLoader(int id, Bundle args) {
        throw new IllegalStateException("Loader ID can't be " + id + ".");
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<D>> loader, AsyncResult<D> data) {
        throw new IllegalStateException("Loader ID can't be " + loader.getId() + ".");
    }
}
