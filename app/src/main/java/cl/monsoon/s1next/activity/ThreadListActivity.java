package cl.monsoon.s1next.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.BaseFragment;
import cl.monsoon.s1next.fragment.ThreadListPagerFragment;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.util.MathUtil;
import cl.monsoon.s1next.widget.FragmentStatePagerAdapter;

/**
 * An Activity representing a list of threads.
 */
public final class ThreadListActivity
        extends BaseActivity
        implements ThreadListPagerFragment.OnPagerInteractionCallback {

    public final static String ARG_FORUM_NAME = "forum_name";
    public final static String ARG_FORUM_ID = "forum_id";
    public final static String ARG_THREADS = "threads";

    private CharSequence mTitle;
    private CharSequence mForumId;
    private int mNumPages;

    /**
     * The {@link FragmentStatePagerAdapter} that will provide
     * fragments for each page of threads.
     */
    private PagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        setNavDrawerIndicatorEnabled(false);

        mTitle = getIntent().getCharSequenceExtra(ARG_FORUM_NAME);
        setTitle(mTitle);
        mForumId = getIntent().getCharSequenceExtra(ARG_FORUM_ID);
        setCount(getIntent().getIntExtra(ARG_THREADS, 1));

        FrameLayout container = (FrameLayout) findViewById(R.id.frame_layout);
        View.inflate(this, R.layout.activity_screen_slide, container);

        ViewPager viewPager = (ViewPager) container.findViewById(R.id.pager);
        mAdapter = new ThreadListPagerAdapter(getFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                showOrHideToolbar(true);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * Implement {@link cl.monsoon.s1next.fragment.ThreadListPagerFragment.OnPagerInteractionCallback}.
     */
    @Override
    public void setCount(int i) {
        mNumPages = MathUtil.divide(i, Config.THREADS_PER_PAGE);

        if (mAdapter != null) {
            runOnUiThread(mAdapter::notifyDataSetChanged);
        }
    }

    /**
     * Return a Fragment corresponding to one of the pages of threads.
     */
    private class ThreadListPagerAdapter extends FragmentStatePagerAdapter {

        private ThreadListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return ThreadListPagerFragment.newInstance(mForumId, i + 1);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (!isNavDrawerOpened()) {
                setTitle(mTitle + "  " + (position + 1));
            }

            super.setPrimaryItem(container, position, object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // We do not reuse Fragment in ViewPager and its retained fragment.
            // May reuse these both later, but it's not cost-effective nowadays.
            // See AbsHttpFragment#onActivityCreated(Bundle).
            if (object instanceof BaseFragment) {
                ((BaseFragment) object).destroyRetainedFragment();
            }

            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return mNumPages;
        }
    }
}
