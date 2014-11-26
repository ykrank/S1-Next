package cl.monsoon.s1next.fragment;

import android.content.Loader;
import android.text.TextUtils;

import com.squareup.okhttp.RequestBody;

import cl.monsoon.s1next.Config;
import cl.monsoon.s1next.model.mapper.ResultWrapper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpPostLoader;

/**
 * Wrap {@link cl.monsoon.s1next.widget.HttpPostLoader} and ProgressDialog.
 */
public abstract class AbsReplyLoaderFragment extends AbsPostLoaderFragment {

    static final int ID_LOADER_GET_AUTHENTICITY_TOKEN = 0;
    static final int ID_LOADER_POST_REPLY = 1;

    private RequestBody mRequestBody;

    /**
     * We need to get authenticity token (formhash) if we haven't.
     * Then post the rely.
     *
     * @see cl.monsoon.s1next.Api#URL_REPLY_HELPER
     */
    void startLoader() {
        int loaderId;
        if (TextUtils.isEmpty(Config.getAuthenticityToken())) {
            loaderId = ID_LOADER_GET_AUTHENTICITY_TOKEN;
        } else {
            loaderId = ID_LOADER_POST_REPLY;
        }

        mLoader = getLoaderManager().getLoader(loaderId);
        if (mLoader == null) {
            mLoader = getLoaderManager().initLoader(loaderId, null, this);
        } else {
            if (loaderId == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
                mLoader.onContentChanged();
            } else {
                if (mLoader instanceof HttpPostLoader) {
                    // pass RequestBody to change post body
                    //noinspection RedundantCast
                    ((HttpPostLoader) mLoader).onContentChanged(mRequestBody);
                } else {
                    throw new ClassCastException(mLoader + " must extend HttpPostLoader.");
                }
            }
        }
        mLoading = true;
    }

    /**
     * @param requestBody used when content changed.
     */
    void startLoader(RequestBody requestBody) {
        this.mRequestBody = requestBody;

        startLoader();
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<ResultWrapper>> loader, AsyncResult<ResultWrapper> data) {
        // don't call super.onLoadFinished(loader, data)

        int id = loader.getId();
        if (id == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
            if (TextUtils.isEmpty(Config.getAuthenticityToken())) {
                throw new IllegalStateException("Authenticity Token can't be empty.");
            }

            startLoader();
        } else if (id == ID_LOADER_POST_REPLY) {
            mLoading = false;
            dismissProgressDialog();
        } else {
            throw new ClassCastException("Loader id can't be " + id + ".");
        }
    }
}
