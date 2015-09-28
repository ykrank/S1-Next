package cl.monsoon.s1next.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.common.base.Preconditions;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.Api;
import cl.monsoon.s1next.data.api.model.Forum;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.util.MathUtil;

/**
 * A Fragment includes {@link android.support.v4.view.ViewPager}
 * to represent each page of thread lists.
 */
public final class ThreadListFragment extends BaseViewPagerFragment
        implements ThreadListPagerFragment.PagerCallback {

    public static final String TAG = ThreadListFragment.class.getName();

    private static final String ARG_FORUM = "forum";

    private String mForumName;
    private String mForumId;

    public static ThreadListFragment newInstance(Forum forum) {
        ThreadListFragment fragment = new ThreadListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_FORUM, forum);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Forum forum = Preconditions.checkNotNull(getArguments().getParcelable(ARG_FORUM));
        mForumName = forum.getName();
        mForumId = forum.getId();
        if (savedInstanceState == null) {
            setTotalPageByThreads(forum.getThreads());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_thread, menu);

        menu.findItem(R.id.menu_page_turning).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_browser:
                IntentUtil.startViewIntentExcludeOurApp(getContext(), Uri.parse(
                        Api.getThreadListUrlForBrowser(mForumId, getCurrentPage())));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    BaseFragmentStatePagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
        return new ThreadListPagerAdapter(fragmentManager);
    }

    @Override
    protected CharSequence getTitleWithoutPosition() {
        return mForumName;
    }

    @Override
    public void setTotalPageByThreads(int threads) {
        setTotalPages(MathUtil.divide(threads, Api.THREADS_PER_PAGE));
    }

    /**
     * Returns a Fragment corresponding to one of the pages of threads.
     */
    private class ThreadListPagerAdapter extends BaseFragmentStatePagerAdapter {

        private ThreadListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return ThreadListPagerFragment.newInstance(mForumId, i + 1);
        }
    }
}
