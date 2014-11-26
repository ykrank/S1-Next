package cl.monsoon.s1next.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.mapper.Deserialization;
import cl.monsoon.s1next.widget.AsyncResult;

/**
 * Use SwipeRefreshLayout to refresh when loading data.
 * <p>
 * This Fragment extends {@link AbsHttpGetFragment} to retain AsyncTask and data.
 */
public abstract class AbsSwipeRefreshFragment<D extends Deserialization> extends AbsHttpGetFragment<D> {

    /**
     * Detect swipe gestures and trigger to refresh data.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResource(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipe_refresh_1, R.color.swipe_refresh_2, R.color.swipe_refresh_3);
        mSwipeRefreshLayout.setOnRefreshListener(this::load);

        // https://code.google.com/p/android/issues/detail?id=77712&q=SwipeRefreshLayout&colspec=ID%20Type%20Status%20Owner%20Summary%20Stars
        mSwipeRefreshLayout.setProgressViewOffset(
                false,
                0,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // indicate that this Fragment would like to influence the set of actions in the action bar
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean hasResult = mHttpGetRetainedFragment.getData() != null;
        boolean isRunning = mHttpGetRetainedFragment.isRunning();

        // set refreshing when mHttpGetRetainedFragment is still loading data
        if (!hasResult || isRunning) {
            mSwipeRefreshLayout.setRefreshing(true);
        }

        // start to load when we haven't data and mHttpGetRetainedFragment isn't running
        if (!hasResult && !isRunning) {
            load();
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
        // disable refresh action when SwipeRefreshLayout is refreshing
        menu.findItem(R.id.menu_refresh).setEnabled(!mHttpGetRetainedFragment.isRunning());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                load();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostExecute(AsyncResult<D> dAsyncResult) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Return the layout resource ID,
     * which used in inflating the view in {@link #onCreate(android.os.Bundle)}.
     */
    abstract int getLayoutResource();

    /**
     * Load data.
     * <p>
     * Subclass should call {@link #executeHttpGet(String, Class)}
     * to execute HTTP GET to load data.
     */
    abstract void load();
}
