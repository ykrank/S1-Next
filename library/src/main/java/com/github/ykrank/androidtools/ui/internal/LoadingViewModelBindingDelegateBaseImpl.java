package com.github.ykrank.androidtools.ui.internal;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.github.ykrank.androidtools.databinding.FragmentRecycleviewBinding;
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel;

public final class LoadingViewModelBindingDelegateBaseImpl
        implements LoadingViewModelBindingDelegate {

    private final FragmentRecycleviewBinding binding;

    public LoadingViewModelBindingDelegateBaseImpl(FragmentRecycleviewBinding binding) {
        this.binding = binding;
    }

    @Override
    public View getRootView() {
        return binding.getRoot();
    }

    @Override
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return binding.swipeRefreshLayout;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return binding.recyclerView;
    }

    @Override
    public void setLoadingViewModel(LoadingViewModel loadingViewModel) {
        binding.setLoadingViewModel(loadingViewModel);
    }
}
