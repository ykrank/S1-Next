package cl.monsoon.s1next.view.internal;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import cl.monsoon.s1next.databinding.FragmentBaseBinding;
import cl.monsoon.s1next.viewmodel.LoadingViewModel;

public final class LoadingViewModelBindingDelegateImpl implements LoadingViewModelBindingDelegate {

    private FragmentBaseBinding mFragmentBaseBinding;

    public LoadingViewModelBindingDelegateImpl(FragmentBaseBinding fragmentBaseBinding) {
        this.mFragmentBaseBinding = fragmentBaseBinding;
    }

    @Override
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mFragmentBaseBinding.swipeRefreshLayout;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mFragmentBaseBinding.recyclerView;
    }

    @Override
    public void setLoadingViewModel(LoadingViewModel loadingViewModel) {
        mFragmentBaseBinding.setLoadingViewModel(loadingViewModel);
    }
}
