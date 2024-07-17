package com.github.ykrank.androidtools.ui.internal;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.github.ykrank.androidtools.ui.adapter.LibBaseRecyclerViewAdapter;
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel;


/**
 * This class represents a delegate which you can bind
 * {@link LoadingViewModel} to different {@link androidx.databinding.ViewDataBinding}s
 * in implementation.
 */
public interface LoadingViewModelBindingDelegate {

    View getRootView();

    SwipeRefreshLayout getSwipeRefreshLayout();

    /**
     * This {@link RecyclerView} should always set a
     * {@link LibBaseRecyclerViewAdapter}
     * implementation, otherwise we can not use
     * {@link LibBaseRecyclerViewAdapter#setHasProgress(boolean)}.
     */
    RecyclerView getRecyclerView();

    void setLoadingViewModel(LoadingViewModel loadingViewModel);
}
