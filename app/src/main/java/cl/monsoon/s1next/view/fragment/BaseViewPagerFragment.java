package cl.monsoon.s1next.view.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.util.StringUtil;
import cl.monsoon.s1next.view.dialog.PageJumpDialogFragment;
import cl.monsoon.s1next.view.internal.PagerCallback;
import cl.monsoon.s1next.widget.FragmentStatePagerAdapter;

/**
 * A base Fragment wraps {@link ViewPager} and provides related methods.
 */
abstract class BaseViewPagerFragment extends Fragment
        implements PageJumpDialogFragment.OnPageJumpedListener, PagerCallback {

    /**
     * The serialization (saved instance state) Bundle key representing
     * the total pages.
     */
    private static final String STATE_TOTAL_PAGES = "total_pages";

    private ViewPager mViewPager;
    private int mTotalPages;

    private MenuItem mMenuPageJump;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_pager, container, false);
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            mTotalPages = 1;
        } else {
            mTotalPages = savedInstanceState.getInt(STATE_TOTAL_PAGES);
        }

        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        // don't use getChildFragmentManager()
        // because we can't retain Fragments (DataRetainedFragment)
        // that are nested in other fragments
        mViewPager.setAdapter(getPagerAdapter(getFragmentManager()));
    }

    @Override
    @CallSuper
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    @CallSuper
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_view_pager, menu);

        mMenuPageJump = menu.findItem(R.id.menu_page_jump);
        preparePageJumpMenu();
    }

    @Override
    @CallSuper
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_page_jump:
                new PageJumpDialogFragment(mTotalPages, getCurrentPage()).show(
                        getChildFragmentManager(), PageJumpDialogFragment.TAG);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @CallSuper
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_TOTAL_PAGES, mTotalPages);
    }

    abstract BaseFragmentStatePagerAdapter getPagerAdapter(FragmentManager fragmentManager);

    public final int getTotalPages() {
        return mTotalPages;
    }

    public final void setTotalPages(int totalPages) {
        this.mTotalPages = totalPages;
        mViewPager.getAdapter().notifyDataSetChanged();
        preparePageJumpMenu();
    }

    final int getCurrentPage() {
        return mViewPager.getCurrentItem();
    }

    final void setCurrentPage(int currentPage) {
        mViewPager.setCurrentItem(currentPage);
    }

    @Override
    @CallSuper
    public void onPageJumped(int position) {
        mViewPager.setCurrentItem(position);
    }

    /**
     * Disables the page jump menu if only has one page.
     */
    private void preparePageJumpMenu() {
        if (mMenuPageJump == null) {
            return;
        }

        if (mTotalPages == 1) {
            mMenuPageJump.setEnabled(false);
        } else {
            mMenuPageJump.setEnabled(true);
        }
    }

    final void setTitleWithPosition(int position) {
        CharSequence titleWithoutPosition = getTitleWithoutPosition();
        if (titleWithoutPosition == null) {
            getActivity().setTitle(null);

            return;
        }

        String titleWithPosition;
        if (ResourceUtil.isRTL(getResources())) {
            titleWithPosition = StringUtil.concatWithTwoSpaces(position + 1, titleWithoutPosition);
        } else {
            titleWithPosition = StringUtil.concatWithTwoSpaces(titleWithoutPosition, position + 1);
        }
        getActivity().setTitle(titleWithPosition);
    }

    @Nullable
    abstract CharSequence getTitleWithoutPosition();

    /**
     * A base {@link FragmentStatePagerAdapter} wraps some implement.
     */
    abstract class BaseFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

        public BaseFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public final int getCount() {
            return mTotalPages;
        }

        @Override
        @CallSuper
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            setTitleWithPosition(position);

            super.setPrimaryItem(container, position, object);
        }

        @Override
        @CallSuper
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof BaseFragment) {
                // We don't reuse Fragment in ViewPager and its retained Fragment
                // because it is not cost-effective nowadays.
                ((BaseFragment) object).destroyRetainedFragment();
            }

            super.destroyItem(container, position, object);
        }
    }
}
