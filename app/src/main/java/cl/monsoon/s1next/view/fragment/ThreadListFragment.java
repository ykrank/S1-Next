package cl.monsoon.s1next.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.S1Service;
import cl.monsoon.s1next.data.api.model.Forum;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.util.MathUtil;
import cl.monsoon.s1next.widget.FragmentStatePagerAdapter;

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
        getActivity().setTitle(getTitleWithPage(1));
        mForumId = forum.getId();
        setTotalPageByThreads(forum.getThreads());

        // don't use getChildFragmentManager()
        // because we can't retain Fragments (DataRetainedFragment)
        // that are nested in other fragments
        PagerAdapter pagerAdapter = new ThreadListPagerAdapter(getFragmentManager());
        ViewPager viewPager = getViewPager();
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_thread, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_browser:
                IntentUtil.startViewIntentExcludeOurApp(getActivity(),
                        Uri.parse(Api.getThreadListUrlForBrowser(mForumId,
                                getViewPager().getCurrentItem())));

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected CharSequence getTitleWithoutPageNumber() {
        return mForumName;
    }

    @Override
    public void setTotalPageByThreads(int threads) {
        setTotalPage(MathUtil.divide(threads, S1Service.THREADS_PER_PAGE));
    }

    /**
     * Returns a Fragment corresponding to one of the pages of threads.
     */
    private class ThreadListPagerAdapter extends FragmentStatePagerAdapter {

        private ThreadListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return getTotalPage();
        }

        @Override
        public Fragment getItem(int i) {
            return ThreadListPagerFragment.newInstance(mForumId, i + 1);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // We don't reuse Fragment in ViewPager and its retained Fragment
            // because it is not cost-effective nowadays.
            ((BaseFragment) object).destroyRetainedFragment();

            super.destroyItem(container, position, object);
        }
    }
}
