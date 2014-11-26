package cl.monsoon.s1next.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import cl.monsoon.s1next.fragment.headless.HttpGetRetainedFragment;
import cl.monsoon.s1next.model.mapper.Deserialization;
import cl.monsoon.s1next.widget.AsyncResult;

/**
 * Wrap {@link cl.monsoon.s1next.fragment.headless.HttpGetRetainedFragment}.
 */
public abstract class AbsHttpGetFragment<D extends Deserialization> extends Fragment implements HttpGetRetainedFragment.AsyncTaskCallback<D> {

    /**
     * Use {@link cl.monsoon.s1next.fragment.headless.HttpGetRetainedFragment} to retain AsyncTask and data.
     */
    HttpGetRetainedFragment<D> mHttpGetRetainedFragment;

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
                    .add(mHttpGetRetainedFragment =
                            new HttpGetRetainedFragment<>(), mRetainedHttpGetFragmentTag).commit();
        } else {
            // post data when configuration change and we already have data
            D data = mHttpGetRetainedFragment.getData();
            if (data != null) {
                onPostExecute(new AsyncResult<>(data));
            }
        }
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
