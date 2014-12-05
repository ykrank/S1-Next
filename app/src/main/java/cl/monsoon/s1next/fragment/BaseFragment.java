package cl.monsoon.s1next.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.activity.BaseActivity;
import cl.monsoon.s1next.fragment.headless.HttpGetRetainedFragment;
import cl.monsoon.s1next.model.mapper.Deserialization;
import cl.monsoon.s1next.util.ObjectUtil;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.MyRecyclerView;

/**
 * A base Fragment which includes the SwipeRefreshLayout to refresh when loading data.
 * And wrap {@link cl.monsoon.s1next.fragment.headless.HttpGetRetainedFragment} to
 * retain {@link HttpGetRetainedFragment.AsyncHttpGetTask} and data when configuration change.
 */
public abstract class BaseFragment<D extends Deserialization>
        extends Fragment
        implements HttpGetRetainedFragment.Callback<D>,
        SwipeRefreshLayout.OnRefreshListener {

    /**
     * Use {@link cl.monsoon.s1next.fragment.headless.HttpGetRetainedFragment}
     * to retain AsyncTask and data.
     */
    private HttpGetRetainedFragment<D> mHttpGetRetainedFragment;

    /**
     * Detect swipe gestures and trigger to refresh data.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mIsProgressbarViewGone;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupSwipeRefreshLayout();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this Fragment would like to
        // influence the set of actions in the ToolBar.
        setHasOptionsMenu(true);

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

        Fragment fragment = fragmentManager.findFragmentByTag(mRetainedHttpGetFragmentTag);
        if (fragment != null) {
            if (fragment instanceof HttpGetRetainedFragment) {
                mHttpGetRetainedFragment = ObjectUtil.uncheckedCast(fragment);
            } else {
                throw new ClassCastException(fragment + " must extend HttpGetRetainedFragment.");
            }
        }

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

    @Override
    public void onResume() {
        super.onResume();

        final boolean hasData = mHttpGetRetainedFragment.getData() != null;
        final boolean isRunning = mHttpGetRetainedFragment.isRunning();

        // refresh when mHttpGetRetainedFragment is still loading data
        if (hasData && isRunning) {
            mSwipeRefreshLayout.setRefreshing(true);
        }

        // Start to load data when we haven't data
        // and mHttpGetRetainedFragment isn't running.
        if (!hasData && !isRunning) {
            onRefresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Disable refresh action when SwipeRefreshLayout is still refreshing.
        menu.findItem(R.id.menu_refresh).setEnabled(
                mSwipeRefreshLayout.isEnabled() && !mHttpGetRetainedFragment.isRunning());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupSwipeRefreshLayout() {
        if (getView() != null) {
            mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh);
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setColorSchemeResources(
                        R.color.swipe_refresh_1, R.color.swipe_refresh_2, R.color.swipe_refresh_3);

                mSwipeRefreshLayout.setOnRefreshListener(this);
                mSwipeRefreshLayout.setEnabled(false);

                return;
            }
        }

        throw new IllegalStateException("Can't set up SwipeRefreshLayout.");
    }

    /**
     * We need to update SwipeRefreshLayout's progress view offset if we have overlay Toolbar.
     */
    private void updateSwipeRefreshProgressViewPosition() {
        if (mSwipeRefreshLayout == null) {
            return;
        }

        int start = getResources().getDimensionPixelSize(R.dimen.swipe_refresh_progress_view_start);
        int end = getResources().getDimensionPixelSize(R.dimen.swipe_refresh_progress_view_end);

        mSwipeRefreshLayout.setProgressViewOffset(false, start, end);
    }

    void setupRecyclerViewPadding(RecyclerView recyclerView, int padding, boolean hasOverlayToolbar) {
        int additionalTopPadding =
                hasOverlayToolbar
                        ? ResourceUtil.getToolbarHeight(getActivity())
                        : 0;
        // +Toolbar's height if has overlay Toolbar
        recyclerView.setPadding(
                0, padding + additionalTopPadding, 0, padding);

        updateSwipeRefreshProgressViewPosition();
    }

    void enableToolbarAutoHideEffect(MyRecyclerView recyclerView) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).enableToolbarAutoHideEffect(recyclerView);
        } else {
            throw new ClassCastException(getActivity() + "must extend BaseActivity.");
        }
    }

    @Override
    public void onPostExecute(AsyncResult<D> dAsyncResult) {
        if (mIsProgressbarViewGone) {
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            mSwipeRefreshLayout.setEnabled(true);
            //noinspection ConstantConditions
            getView().findViewById(R.id.progressbar).setVisibility(View.GONE);
            mIsProgressbarViewGone = true;
        }
    }

    void execute(String url, Class<D> clazz) {
        mHttpGetRetainedFragment.execute(url, clazz);
    }

    public void destroyRetainedFragment() {
        if (mHttpGetRetainedFragment != null) {
            getFragmentManager().beginTransaction().
                    remove(mHttpGetRetainedFragment).commit();
        }
    }
}
