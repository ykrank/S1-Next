package cl.monsoon.s1next.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.Config;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Forum;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.util.MathUtil;
import cl.monsoon.s1next.util.StringUtil;
import cl.monsoon.s1next.widget.FragmentStatePagerAdapter;

/**
 * A Fragment which includes {@link android.support.v4.view.ViewPager}
 * to represent each page of thread lists.
 */
public final class ThreadListFragment extends Fragment implements ThreadListPagerFragment.PagerCallback {

    public static final String TAG = ThreadListFragment.class.getSimpleName();

    private static final String ARG_FORUM = "forum";

    private String mForumTitle;
    private String mForumId;
    private int mTotalPages;

    /**
     * The {@link FragmentStatePagerAdapter} will provide
     * fragments for each page of threads.
     */
    private PagerAdapter mAdapter;
    private ViewPager mViewPager;

    public static ThreadListFragment newInstance(Forum forum) {
        ThreadListFragment fragment = new ThreadListFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_FORUM, forum);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.screen_slide, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Forum forum = getArguments().getParcelable(ARG_FORUM);
        mForumTitle = forum.getName();
        getActivity().setTitle(StringUtil.concatWithTwoSpaces(mForumTitle, 1));
        mForumId = forum.getId();
        setTotalPageByThreads(forum.getThreads());

        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        // don't use getChildFragmentManager()
        // because we can't retain Fragments (DataRetainedFragment)
        // that are nested in other fragments
        mAdapter = new ThreadListPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().setTitle(StringUtil.concatWithTwoSpaces(mForumTitle, position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
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
                        Uri.parse(Api.getThreadListUrlForBrowser(mForumId, mViewPager.getCurrentItem())));

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Implements {@link ThreadListPagerFragment.PagerCallback}.
     */
    @Override
    public void setTotalPageByThreads(int threads) {
        mTotalPages = MathUtil.divide(threads, Config.THREADS_PER_PAGE);

        if (mAdapter != null) {
            getActivity().runOnUiThread(mAdapter::notifyDataSetChanged);
        }
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
            return mTotalPages;
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
