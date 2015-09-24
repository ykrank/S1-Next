package cl.monsoon.s1next.view.internal;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cl.monsoon.s1next.databinding.FragmentBaseCardViewContainerBinding;
import cl.monsoon.s1next.viewmodel.LoadingViewModel;

public final class LoadingViewModelBindingDelegateBaseCardViewContainerImpl
        implements LoadingViewModelBindingDelegate {

    private final FragmentBaseCardViewContainerBinding mFragmentBaseCardViewContainerBinding;

    public LoadingViewModelBindingDelegateBaseCardViewContainerImpl(
            FragmentBaseCardViewContainerBinding fragmentBaseCardViewContainerBinding) {
        this.mFragmentBaseCardViewContainerBinding = fragmentBaseCardViewContainerBinding;
    }

    @Override
    public View getRootView() {
        return mFragmentBaseCardViewContainerBinding.getRoot();
    }

    @Override
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mFragmentBaseCardViewContainerBinding.swipeRefreshLayout;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mFragmentBaseCardViewContainerBinding.recyclerView;
    }

    @Override
    public void setLoadingViewModel(LoadingViewModel loadingViewModel) {
        mFragmentBaseCardViewContainerBinding.setLoadingViewModel(loadingViewModel);
    }
}
