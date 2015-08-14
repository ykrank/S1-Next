package cl.monsoon.s1next.view.internal;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import cl.monsoon.s1next.viewmodel.LoadingViewModel;

/**
 * This class represents a delegate which you can bind
 * {@link LoadingViewModel} to different {@link android.databinding.ViewDataBinding}s
 * in implementation.
 */
public interface LoadingViewModelBindingDelegate {

    SwipeRefreshLayout getSwipeRefreshLayout();

    RecyclerView getRecyclerView();

    void setLoadingViewModel(LoadingViewModel loadingViewModel);
}
