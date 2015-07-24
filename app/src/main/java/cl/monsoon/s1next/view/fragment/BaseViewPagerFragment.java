package cl.monsoon.s1next.view.fragment;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
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
import cl.monsoon.s1next.viewmodel.ViewPagerViewModel;

/**
 * A base Fragment wraps {@link ViewPager} and provides related methods.
 */
public abstract class BaseViewPagerFragment extends Fragment
        implements ViewPager.OnPageChangeListener,
        PageTurningDialogFragment.OnPageTurnedListener {

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
        viewModel.totalPage.set(1);
        mFragmentViewPagerBinding.setViewPagerViewModel(viewModel);

        mFragmentViewPagerBinding.viewPager.addOnPageChangeListener(this);
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
                new PageTurningDialogFragment(getTotalPage(), getViewPager().getCurrentItem())
                        .show(getChildFragmentManager(), PageTurningDialogFragment.TAG);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    final ViewPager getViewPager() {
        return mFragmentViewPagerBinding.viewPager;
    }

    final int getTotalPage() {
        return mFragmentViewPagerBinding.getViewPagerViewModel().totalPage.get();
    }

    final void setTotalPage(int page) {
        mFragmentViewPagerBinding.getViewPagerViewModel().totalPage.set(page);
        preparePageTurningMenu();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    @CallSuper
    public void onPageSelected(int position) {
        // position is zero-based
        getActivity().setTitle(getTitleWithPage(position + 1));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    @CallSuper
    public void onPageTurned(int position) {
        getViewPager().setCurrentItem(position);
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

    final CharSequence getTitleWithPage(int pageNumber) {
        if (ResourceUtil.isRTL(getResources())) {
            return StringUtil.concatWithTwoSpaces(pageNumber, getTitleWithoutPageNumber());
        } else {
            return StringUtil.concatWithTwoSpaces(getTitleWithoutPageNumber(), pageNumber);
        }
    }

    abstract CharSequence getTitleWithoutPageNumber();

    @BindingAdapter("totalPage")
    public static void setTotalPage(ViewPager viewPager, int totalPage) {
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter != null) {
            pagerAdapter.notifyDataSetChanged();
        }
    }
}
