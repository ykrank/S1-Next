package cl.monsoon.s1next.fragment;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.activity.BaseActivity;
import cl.monsoon.s1next.fragment.headless.DataRetainedFragment;
import cl.monsoon.s1next.model.Extractable;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.view.InsetsFrameLayout;
import cl.monsoon.s1next.widget.AsyncResult;

/**
 * A base Fragment which includes the SwipeRefreshLayout to refresh when loading data.
 * Also wraps {@link LoaderManager.LoaderCallbacks} to load data asynchronously.
 * <p>
 * We must reuse or destroy {@link #mDataRetainedFragment} (calling {@link BaseFragment#destroyRetainedFragment()})
 * if used in {@link android.support.v4.view.ViewPager}
 * otherwise we would lost {@link #mDataRetainedFragment} and cause memory leak.
 */
public abstract class BaseFragment<D extends Extractable> extends Fragment
        implements InsetsFrameLayout.OnInsetsCallback,
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<AsyncResult<D>> {

    private static final int ID_LOADER = 0;

    /**
     * The serialization (saved instance state) Bundle key representing whether
     * {@link Loader} is loading data when configuration changes.
     */
    private static final String STATE_IS_LOADER_LOADING = "is_loader_loading";

    private InsetsCallback mInsetsCallback;

    /**
     * Detects swipe gestures and triggers to refresh data.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mLoading;

    /**
     * We use retained Fragment to retain data when configuration changes
     * due to https://stackoverflow.com/questions/15897547/loader-unable-to-retain-itself-during-certain-configuration-change
     */
    private DataRetainedFragment<D> mDataRetainedFragment;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupSwipeRefreshLayout();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicates that this Fragment would like to
        // influence the set of actions in the Toolbar.
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mLoading = savedInstanceState.getBoolean(STATE_IS_LOADER_LOADING);
        }

        // because we can't retain Fragments that are nested in other Fragments
        // so we need to confirm this Fragment has unique tag in order to compose
        // a new unique tag for its retained Fragment.
        // Without this, we could get its retained Fragment back.
        String thisFragmentTag = getTag();
        if (thisFragmentTag == null) {
            throw new IllegalStateException("Must add a tag to" + this + ".");
        }

        String dataRetainedFragmentTag = DataRetainedFragment.TAG + "_" + thisFragmentTag;
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(dataRetainedFragmentTag);
        if (fragment == null) {
            mDataRetainedFragment = new DataRetainedFragment<>();
            fragmentManager.beginTransaction().add(mDataRetainedFragment,
                    dataRetainedFragmentTag).commit();

            getLoaderManager().initLoader(ID_LOADER, null, this);
        } else {
            mDataRetainedFragment = (DataRetainedFragment) fragment;

            boolean loading = mLoading;
            if (mDataRetainedFragment.getData() != null) {
                // get data back from retained Fragment when configuration changes
                onLoadFinished(null, new AsyncResult<>(mDataRetainedFragment.getData()));
            }
            mLoading = loading;

            // mDataRetainedFragment.getThreadList() = null and mLoading = false
            // if this app was killed by system before
            if (mDataRetainedFragment.getData() == null || mLoading) {
                getLoaderManager().initLoader(ID_LOADER, null, this);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // refresh when Loader is still loading data
        if (mLoading && mSwipeRefreshLayout.isEnabled()) {
            // see https://code.google.com/p/android/issues/detail?id=77712
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mInsetsCallback = (InsetsCallback) activity;
        mInsetsCallback.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mInsetsCallback.unregister(this);
        mInsetsCallback = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_IS_LOADER_LOADING, mLoading);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_base, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Disables the refresh menu when Loader is loading data.
        menu.findItem(R.id.menu_refresh).setEnabled(!mLoading);
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

    void onInsetsChanged() {
        onInsetsChanged(mInsetsCallback.getSystemWindowInsets());
    }

    void enableToolbarAndFabAutoHideEffect(RecyclerView recyclerView) {
        ((BaseActivity) getActivity()).enableToolbarAndFabAutoHideEffect(recyclerView);
    }

    private void setupSwipeRefreshLayout() {
        if (getView() != null) {
            mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh);
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_1,
                        R.color.swipe_refresh_2, R.color.swipe_refresh_3, R.color.swipe_refresh_4);

                mSwipeRefreshLayout.setOnRefreshListener(this);
                mSwipeRefreshLayout.setEnabled(false);

                return;
            }
        }

        throw new IllegalStateException("Can't set up SwipeRefreshLayout.");
    }

    /**
     * We need to update SwipeRefreshLayout's progress view offset
     * if we have overlay status bar & Toolbar.
     */
    private void updateSwipeRefreshProgressViewPosition(Rect insects) {
        if (mSwipeRefreshLayout == null) {
            return;
        }

        int start = insects.top
                + getResources().getDimensionPixelSize(R.dimen.swipe_refresh_progress_view_start);
        int end = insects.top
                + getResources().getDimensionPixelSize(R.dimen.swipe_refresh_progress_view_end);

        mSwipeRefreshLayout.setProgressViewOffset(false, start, end);
    }

    /**
     * @see cl.monsoon.s1next.activity.BaseActivity#onInsetsChanged(android.graphics.Rect)
     */
    void setRecyclerViewPadding(RecyclerView recyclerView, Rect insets, int padding) {
        int toolbarHeight = ResourceUtil.getToolbarHeight();
        recyclerView.setPadding(0, padding + insets.top + toolbarHeight, 0, padding);
        updateSwipeRefreshProgressViewPosition(insets);
    }

    void setSwipeRefreshLayoutEnabled(boolean enabled) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enabled);
        }
    }

    Boolean isLoading() {
        return mLoading;
    }

    @Override
    public void onRefresh() {
//        Loader loader = getLoaderManager().getLoader(ID_LOADER);
//        if (loader == null) {
//            getLoaderManager().initLoader(ID_LOADER, null, this);
//        } else {
//            loader.onContentChanged();
//            mLoading = true;
//        }
        // Bug: it looks loader.onContentChanged() doesn't work
        // when orientation changes
        getLoaderManager().restartLoader(ID_LOADER, null, this);
    }

    @Override
    public Loader<AsyncResult<D>> onCreateLoader(int id, Bundle args) {
        mLoading = true;

        return null;
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<D>> loader, AsyncResult<D> asyncResult) {
        mDataRetainedFragment.setData(asyncResult.data);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(true);
        //noinspection ConstantConditions
        getView().findViewById(R.id.progressbar).setVisibility(View.GONE);
        mLoading = false;
    }

    @Override
    public void onLoaderReset(Loader<AsyncResult<D>> loader) {

    }

    public void destroyRetainedFragment() {
        if (mDataRetainedFragment != null) {
            getFragmentManager().beginTransaction().remove(mDataRetainedFragment).commit();
        }
    }

    public interface InsetsCallback {

        void register(InsetsFrameLayout.OnInsetsCallback onInsetsCallback);

        void unregister(InsetsFrameLayout.OnInsetsCallback onInsetsCallback);

        Rect getSystemWindowInsets();
    }
}
