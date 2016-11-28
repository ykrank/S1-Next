package me.ykrank.s1next.view.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import me.ykrank.s1next.R;
import me.ykrank.s1next.util.ErrorUtil;
import me.ykrank.s1next.util.ResourceUtil;
import me.ykrank.s1next.util.StringUtil;
import me.ykrank.s1next.view.dialog.PageJumpDialogFragment;
import me.ykrank.s1next.view.internal.PagerCallback;
import me.ykrank.s1next.widget.FragmentStatePagerAdapter;

/**
 * A base Fragment wraps {@link ViewPager} and provides related methods.
 */
abstract class BaseViewPagerFragment extends BaseFragment
        implements PageJumpDialogFragment.OnPageJumpedListener, PagerCallback {

    /**
     * The serialization (saved instance state) Bundle key representing
     * the total pages.
     */
    private static final String STATE_TOTAL_PAGES = "total_pages";

    protected ViewPager mViewPager;
    protected BaseFragmentStatePagerAdapter mAdapter;
    private int mTotalPages;

    private MenuItem mMenuPageJump;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_pager, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mTotalPages = 1;
        } else {
            mTotalPages = savedInstanceState.getInt(STATE_TOTAL_PAGES);
        }
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        loadViewPager();
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

    void onError(Throwable throwable) {
        showShortSnackbar(ErrorUtil.parse(throwable));
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
    
    final void loadViewPager(){
        // don't use getChildFragmentManager()
        // because we can't retain Fragments (DataRetainedFragment)
        // that are nested in other fragments
        mAdapter = getPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
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
    abstract class BaseFragmentStatePagerAdapter<T extends BaseRecyclerViewFragment> extends FragmentStatePagerAdapter<T> {

        private T mCurrentFragment;

        public BaseFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        public T getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public final int getCount() {
            return mTotalPages;
        }

        @Override
        @CallSuper
        public void setPrimaryItem(ViewGroup container, int position, T fragment) {
            setTitleWithPosition(position);
            if (mCurrentFragment != fragment) {
                mCurrentFragment = fragment;
            }
            
            super.setPrimaryItem(container, position, fragment);
        }

        @Override
        @CallSuper
        public void destroyItem(ViewGroup container, int position, T fragment) {
            if (fragment != null) {
                // We don't reuse Fragment in ViewPager and its retained Fragment
                // because it is not cost-effective nowadays.
                fragment.destroyRetainedFragment();
            }

            super.destroyItem(container, position, fragment);
        }
    }
}
