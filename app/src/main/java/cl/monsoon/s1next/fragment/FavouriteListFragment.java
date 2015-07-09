package cl.monsoon.s1next.fragment;

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
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.util.StringUtil;
import cl.monsoon.s1next.widget.FragmentStatePagerAdapter;

/**
 * A Fragment which includes {@link android.support.v4.view.ViewPager}
 * to represent each page of favourite lists.
 * <p>
 * Similar to {@link cl.monsoon.s1next.fragment.ThreadListFragment}.
 */
public final class FavouriteListFragment extends Fragment implements FavouriteListPagerFragment.PagerCallback {

    public static final String TAG = FavouriteListFragment.class.getSimpleName();

    private static final String STATE_TOTAL_PAGES = "total_pages";

    private CharSequence mTitle;
    private int mTotalPages = 1;

    /**
     * The {@link FragmentStatePagerAdapter} will provide
     * fragments for each page of favourites.
     */
    private PagerAdapter mAdapter;
    private ViewPager mViewPager;

    private MenuItem mMenuPageTurning;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.screen_slide, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitle = getText(R.string.favourites);
        getActivity().setTitle(StringUtil.concatWithTwoSpaces(mTitle, 1));
        if (savedInstanceState != null) {
            mTotalPages = savedInstanceState.getInt(STATE_TOTAL_PAGES);
        }

        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mAdapter = new FavouriteListPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().setTitle(StringUtil.concatWithTwoSpaces(mTitle, position + 1));
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_TOTAL_PAGES, mTotalPages);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_favourites, menu);

        mMenuPageTurning = menu.findItem(R.id.menu_page_turning);
        preparePageTurningMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_page_turning:
                new FavouriteListPageTurningDialogFragment(mViewPager.getCurrentItem(), mTotalPages)
                        .show(getChildFragmentManager(), PageTurningDialogFragment.TAG);

                return true;
            case R.id.menu_browser:
                IntentUtil.startViewIntentExcludeOurApp(getActivity(),
                        Uri.parse(Api.getFavouritesListUrlForBrowser(mViewPager.getCurrentItem() + 1)));

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Disables the page turning menu if only has one page.
     */
    private void preparePageTurningMenu() {
        if (mMenuPageTurning == null) {
            return;
        }

        if (mTotalPages == 1) {
            mMenuPageTurning.setEnabled(false);
        } else {
            mMenuPageTurning.setEnabled(true);
        }
    }

    /**
     * Implements {@link FavouriteListPagerFragment.PagerCallback}.
     */
    @Override
    public void setTotalPage(int totalPage) {
        this.mTotalPages = totalPage;

        preparePageTurningMenu();

        if (mAdapter != null) {
            getActivity().runOnUiThread(mAdapter::notifyDataSetChanged);
        }
    }

    /**
     * Returns a Fragment corresponding to one of the pages of favourites.
     */
    private class FavouriteListPagerAdapter extends FragmentStatePagerAdapter {

        private FavouriteListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mTotalPages;
        }

        @Override
        public Fragment getItem(int i) {
            return FavouriteListPagerFragment.newInstance(i + 1);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((BaseFragment) object).destroyRetainedFragment();

            super.destroyItem(container, position, object);
        }
    }

    public static class FavouriteListPageTurningDialogFragment extends PageTurningDialogFragment {
        public FavouriteListPageTurningDialogFragment() {
            // Every fragment must have an empty constructor, so it
            // can be instantiated when restoring its activity's state.
        }

        @SuppressWarnings("ValidFragment")
        public FavouriteListPageTurningDialogFragment(int currentPage, int totalPages) {
            super(currentPage, totalPages);
        }

        @Override
        protected void onPageTurning(int page) {
            ((FavouriteListFragment) getParentFragment()).mViewPager.setCurrentItem(page);
        }
    }
}
