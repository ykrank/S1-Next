package cl.monsoon.s1next.fragment;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.activity.BaseActivity;
import cl.monsoon.s1next.fragment.headless.HttpGetRetainedFragment;
import cl.monsoon.s1next.model.Extractable;
import cl.monsoon.s1next.util.ObjectUtil;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.InsetsFrameLayout;
import cl.monsoon.s1next.widget.MyRecyclerView;

/**
 * A base Fragment which includes the SwipeRefreshLayout to refresh when loading data.
 * And wraps {@link cl.monsoon.s1next.fragment.headless.HttpGetRetainedFragment} to
 * retain {@link HttpGetRetainedFragment.AsyncHttpGetTask} and data when configuration changes.
 * <p>
 * We must reuse or destroy mHttpGetRetainedFragment (calling {@link BaseFragment#destroyRetainedFragment()})
 * if used in {@link android.support.v4.view.ViewPager}
 * otherwise we would lost mHttpGetRetainedFragment and cause memory leak.
 */
public abstract class BaseFragment<D extends Extractable>
        extends Fragment
        implements HttpGetRetainedFragment.Callback<D>,
        SwipeRefreshLayout.OnRefreshListener,
        InsetsFrameLayout.OnInsetsCallback {

    /**
     * Uses {@link cl.monsoon.s1next.fragment.headless.HttpGetRetainedFragment}
     * to retain AsyncTask and data.
     */
    private HttpGetRetainedFragment<D> mHttpGetRetainedFragment;

    /**
     * Detects swipe gestures and triggers to refresh data.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupSwipeRefreshLayout();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicates that this Fragment would like to
        // influence the set of actions in the Toolbar.
        setHasOptionsMenu(true);

        String thisFragmentTag = getTag();
        if (thisFragmentTag == null) {
            throw new IllegalStateException("Must add a tag to" + this + ".");
        }

        // In order to let Fragment which created from FragmentStatePagerAdapter
        // to get its mHttpGetRetainedFragment back,
        // we should combine prefix with its host Fragment tag.
        String retainedHttpGetFragmentTag = HttpGetRetainedFragment.TAG_PREFIX + thisFragmentTag;

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(retainedHttpGetFragmentTag);

        if (fragment != null) {
            mHttpGetRetainedFragment =
                    ObjectUtil.uncheckedCast(
                            ObjectUtil.cast(fragment, HttpGetRetainedFragment.class));
        }

        if (mHttpGetRetainedFragment == null) {
            mHttpGetRetainedFragment = new HttpGetRetainedFragment<>();
            fragmentManager.beginTransaction()
                    .add(mHttpGetRetainedFragment, retainedHttpGetFragmentTag).commit();
        } else {
            // post data when configuration changes and we already have data
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
        if (hasData && isRunning && mSwipeRefreshLayout.isEnabled()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }

        // Starts to load data when we haven't data
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ObjectUtil.cast(activity, BaseActivity.class).registerInsetsCallback(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        ObjectUtil.cast(getActivity(), BaseActivity.class).unregisterInsetsCallback(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Disables the refresh menu when SwipeRefreshLayout is still refreshing.
        menu.findItem(R.id.menu_refresh).setEnabled(!isRefreshing());
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
                        R.color.swipe_refresh_1, R.color.swipe_refresh_2,
                        R.color.swipe_refresh_3, R.color.swipe_refresh_4);

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
    private void updateSwipeRefreshProgressViewPosition(@NonNull Rect insects) {
        if (mSwipeRefreshLayout == null) {
            return;
        }

        int start =
                insects.top
                        + getResources().getDimensionPixelSize(R.dimen.swipe_refresh_progress_view_start);
        int end =
                insects.top
                        + getResources().getDimensionPixelSize(R.dimen.swipe_refresh_progress_view_end);

        mSwipeRefreshLayout.setProgressViewOffset(false, start, end);
    }

    /**
     * @see cl.monsoon.s1next.activity.BaseActivity#onInsetsChanged(android.graphics.Rect)
     */
    void setRecyclerViewPadding(RecyclerView recyclerView, @NonNull Rect insets, int padding) {
        int toolbarHeight =
                ResourceUtil.getToolbarHeight();
        recyclerView.setPadding(0, padding + insets.top + toolbarHeight, 0, padding);

        updateSwipeRefreshProgressViewPosition(insets);
    }

    void onInsetsChanged() {
        onInsetsChanged(ObjectUtil.cast(getActivity(), BaseActivity.class).getSystemWindowInsets());
    }

    void enableToolbarAndFabAutoHideEffect(MyRecyclerView recyclerView, @Nullable RecyclerView.OnScrollListener onScrollListener) {
        ObjectUtil.cast(
                getActivity(),
                BaseActivity.class).enableToolbarAndFabAutoHideEffect(recyclerView, onScrollListener);
    }

    boolean isRefreshing() {
        return (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isEnabled())
                || (mHttpGetRetainedFragment != null && mHttpGetRetainedFragment.isRunning());
    }

    void setSwipeRefreshLayoutEnabled(boolean enabled) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enabled);
        }
    }

    @Override
    public void onPostExecute(AsyncResult<D> dAsyncResult) {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(true);
        //noinspection ConstantConditions
        getView().findViewById(R.id.progressbar).setVisibility(View.GONE);
    }

    void execute(String url, Class<D> clazz) {
        mHttpGetRetainedFragment.execute(url, clazz);
    }

    public void destroyRetainedFragment() {
        if (mHttpGetRetainedFragment != null) {
            getFragmentManager().beginTransaction().remove(mHttpGetRetainedFragment).commit();
        }
    }
    protected Map<String, Object> parseUrl(String url){
        Pattern p = null;
        Map map = new HashMap();
        if (url.matches(".*tid=.*")) {
            url = url.substring(url.indexOf("tid"));
            String[] temp = url.split("&");
            for (String t : temp) {
                //[tid=13213,page=1]
                map.put(t.substring(0, t.indexOf("=")).trim(), t.substring(t.indexOf("=") + 1, t.length()).trim());
            }
        } else if (url.matches(".*thread-\\d{1,}-\\d{1,}-\\d{1,}.*")) {
            p = Pattern.compile("thread-\\d{1,}-\\d{1,}-\\d{1,}");
            Matcher m = p.matcher(url);
            if (m.find()) {
                url = url.substring(m.start(), m.end());
            }
            String[] array = url.split("-");
            map.put("tid", array[1]);
            map.put("page", array[2]);
        } else if (url.matches("\\d{1,}-\\d{1,}-\\d{1,}")) {
            String[] array = url.split("-");
            map.put("tid", array[0]);
            map.put("page", array[1]);
        } else if (url.matches("\\d{1,}-\\d{1,}")) {
            String[] array = url.split("-");
            map.put("tid", array[0]);
            map.put("page", array[1]);
        } else if (url.matches("\\d{1,}")) {
            map.put("tid", url);
        } else {
            return null;
        }
        return map;
    }
}
