package cl.monsoon.s1next.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.monsoon.s1next.fragment.headless.HttpGetRetainedFragment;
import cl.monsoon.s1next.model.mapper.Deserialization;
import cl.monsoon.s1next.widget.AsyncResult;

/**
 * Wrap {@link cl.monsoon.s1next.fragment.headless.HttpGetRetainedFragment}.
 */
public abstract class AbsHttpGetFragment<D extends Deserialization> extends Fragment implements HttpGetRetainedFragment.AsyncTaskCallback<D> {

    /**
     * The serialization (saved instance state) Bundle key representing
     * the tag of {@link #mHttpGetRetainedFragment}.
     */
    private static final String STATE_RETAINED_HTTP_GET_FRAGMENT_TAG = "retained_http_get_fragment_tag";

    /**
     * Use {@link cl.monsoon.s1next.fragment.headless.HttpGetRetainedFragment} to retain AsyncTask and data.
     */
    HttpGetRetainedFragment<D> mHttpGetRetainedFragment;
    private String mRetainedHttpGetFragmentTag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRetainedHttpGetFragmentTag =
                    savedInstanceState.getString(STATE_RETAINED_HTTP_GET_FRAGMENT_TAG);
        }

        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fragmentManager = getFragmentManager();

        String thisFragmentTag = getTag();
        if (thisFragmentTag == null) {
            throw new IllegalStateException("Must add a tag to" + this + ".");
        }

        // In order to let Fragment which create from FragmentStatePagerAdapter
        // to reuse mHttpGetRetainedFragment (get its mHttpGetRetainedFragment back),
        // we combine prefix with its host Fragment tag.
        // Be sure we should reuse its host Fragment in FragmentStatePagerAdapter,
        // or destroy mHttpGetRetainedFragment on FragmentStatePagerAdapter#destroyItem(ViewGroup, int, Object)
        // otherwise we would lost mHttpGetRetainedFragment and cause memory leak.
        String mRetainedHttpGetFragmentTag = HttpGetRetainedFragment.TAG_PREFIX + thisFragmentTag;

        //noinspection unchecked
        mHttpGetRetainedFragment =
                (HttpGetRetainedFragment<D>)
                        fragmentManager.findFragmentByTag(mRetainedHttpGetFragmentTag);

        if (mHttpGetRetainedFragment == null) {
            getFragmentManager().beginTransaction()
                    .add(mHttpGetRetainedFragment = new HttpGetRetainedFragment<>()
                            , mRetainedHttpGetFragmentTag)
                    .commit();
        } else {
            // post data when configuration change and we already have data
            D data = mHttpGetRetainedFragment.getData();
            if (data != null) {
                onPostExecute(new AsyncResult<>(data));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_RETAINED_HTTP_GET_FRAGMENT_TAG, mRetainedHttpGetFragmentTag);
    }

    /**
     * Set data on the UI thread.
     * See {@see android.os.AsyncTask#onPostExecute(Object)}
     */
    @Override
    public abstract void onPostExecute(AsyncResult<D> dAsyncResult);

    /**
     * Execute AsyncTask.
     */
    final void executeHttpGet(String url, Class<D> clazz) {
        mHttpGetRetainedFragment.execute(url, clazz);
    }

    public void destroyRetainedFragment() {
        if (mHttpGetRetainedFragment != null) {
            getFragmentManager().beginTransaction().
                    remove(mHttpGetRetainedFragment).commit();
        }
    }
}
