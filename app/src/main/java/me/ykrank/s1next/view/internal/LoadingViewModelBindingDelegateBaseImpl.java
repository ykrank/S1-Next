package me.ykrank.s1next.view.internal;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.github.ykrank.androidtools.ui.internal.LoadingViewModelBindingDelegate;
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel;

import me.ykrank.s1next.databinding.FragmentBaseBinding;

public final class LoadingViewModelBindingDelegateBaseImpl
        implements LoadingViewModelBindingDelegate {

    private final FragmentBaseBinding binding;

    public LoadingViewModelBindingDelegateBaseImpl(FragmentBaseBinding binding) {
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
