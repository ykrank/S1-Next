package cl.monsoon.s1next.view.internal;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cl.monsoon.s1next.viewmodel.LoadingViewModel;

/**
 * This class represents a delegate which you can bind
 * {@link LoadingViewModel} to different {@link android.databinding.ViewDataBinding}s
 * in implementation.
 */
public interface LoadingViewModelBindingDelegate {

    View getRootView();

    SwipeRefreshLayout getSwipeRefreshLayout();

    /**
     * This {@link RecyclerView} should always set a
     * {@link cl.monsoon.s1next.view.adapter.BaseRecyclerViewAdapter}
     * implementation, otherwise we can not use
     * {@link cl.monsoon.s1next.view.adapter.BaseRecyclerViewAdapter#setHasProgress(boolean)}.
     */
    RecyclerView getRecyclerView();

    void setLoadingViewModel(LoadingViewModel loadingViewModel);
}
