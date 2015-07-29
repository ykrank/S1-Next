package cl.monsoon.s1next.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
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
import cl.monsoon.s1next.databinding.FragmentViewPagerBinding;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.util.StringUtil;
import cl.monsoon.s1next.view.dialog.PageTurningDialogFragment;
import cl.monsoon.s1next.view.internal.PagerCallback;
import cl.monsoon.s1next.viewmodel.ViewPagerViewModel;
import cl.monsoon.s1next.widget.FragmentStatePagerAdapter;

/**
 * A base Fragment wraps {@link ViewPager} and provides related methods.
 */
public abstract class BaseViewPagerFragment extends Fragment
        implements PageTurningDialogFragment.OnPageTurnedListener,
        PagerCallback {

    /**
     * The serialization (saved instance state) Bundle key representing
     * the total pages.
     */
    private static final String STATE_TOTAL_PAGES = "total_pages";

    private FragmentViewPagerBinding mFragmentViewPagerBinding;

    private MenuItem mMenuPageTurning;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentViewPagerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_pager,
                container, false);
        return mFragmentViewPagerBinding.getRoot();
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPagerViewModel viewModel = new ViewPagerViewModel();
        if (savedInstanceState != null) {
            viewModel.totalPage.set(savedInstanceState.getInt(STATE_TOTAL_PAGES));
        } else {
            viewModel.totalPage.set(1);
        }
        mFragmentViewPagerBinding.setViewPagerViewModel(viewModel);

        // don't use getChildFragmentManager()
        // because we can't retain Fragments (DataRetainedFragment)
        // that are nested in other fragments
        mFragmentViewPagerBinding.viewPager.setAdapter(getPagerAdapter(getFragmentManager()));
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
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_view_pager, menu);

        mMenuPageTurning = menu.findItem(R.id.menu_page_turning);
        preparePageTurningMenu();
    }

    @Override
    @CallSuper
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_page_turning:
                new PageTurningDialogFragment(getTotalPage(), getCurrentPage()).show(
                        getChildFragmentManager(), PageTurningDialogFragment.TAG);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    @CallSuper
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_TOTAL_PAGES, getTotalPage());
    }

    abstract BaseFragmentStatePagerAdapter getPagerAdapter(FragmentManager fragmentManager);

    private int getTotalPage() {
        return mFragmentViewPagerBinding.getViewPagerViewModel().totalPage.get();
    }

    public final void setTotalPage(int page) {
        mFragmentViewPagerBinding.getViewPagerViewModel().totalPage.set(page);
        preparePageTurningMenu();
    }

    final int getCurrentPage() {
        return mFragmentViewPagerBinding.viewPager.getCurrentItem();
    }

    @Override
    @CallSuper
    public void onPageTurned(int position) {
        mFragmentViewPagerBinding.viewPager.setCurrentItem(position);
    }

    /**
     * Disables the page turning menu if only has one page.
     */
    private void preparePageTurningMenu() {
        if (mMenuPageTurning == null) {
            return;
        }

        if (getTotalPage() == 1) {
            mMenuPageTurning.setEnabled(false);
        } else {
            mMenuPageTurning.setEnabled(true);
        }
    }

    private void setTitleWithPosition(int position) {
        String titleWithPosition;
        if (ResourceUtil.isRTL(getResources())) {
            titleWithPosition = StringUtil.concatWithTwoSpaces(position + 1,
                    getTitleWithoutPosition());
        } else {
            titleWithPosition = StringUtil.concatWithTwoSpaces(getTitleWithoutPosition(),
                    position + 1);
        }

        getActivity().setTitle(titleWithPosition);
    }

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
            return getTotalPage();
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
