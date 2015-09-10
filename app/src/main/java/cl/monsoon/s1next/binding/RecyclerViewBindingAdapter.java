package cl.monsoon.s1next.binding;

import android.databinding.BindingAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import cl.monsoon.s1next.view.adapter.BaseRecyclerViewAdapter;
import cl.monsoon.s1next.viewmodel.LoadingViewModel;

public final class RecyclerViewBindingAdapter {

    private RecyclerViewBindingAdapter() {}

    @BindingAdapter("loadingFirstTime")
    public static void setHasProgress(RecyclerView recyclerView, Boolean oldIsLoadingFirstTime, Boolean newIsLoadingFirstTime) {
        if (newIsLoadingFirstTime != oldIsLoadingFirstTime) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null && adapter instanceof BaseRecyclerViewAdapter) {
                BaseRecyclerViewAdapter baseRecyclerViewAdapter = (BaseRecyclerViewAdapter) adapter;
                baseRecyclerViewAdapter.setHasProgress(newIsLoadingFirstTime);
                baseRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Disables nested scrolling if all child views are fully visible when current
     * {@link LoadingViewModel#loading} status changed.
     */
    @BindingAdapter("loading")
    public static void setNestedScrollingEnabled(RecyclerView recyclerView, @LoadingViewModel.LoadingDef int loading) {
        switch (loading) {
            case LoadingViewModel.LOADING_FIRST_TIME:
                // disable nested scrolling because we only show a Progress
                recyclerView.setNestedScrollingEnabled(false);
                break;
            case LoadingViewModel.LOADING_SWIPE_REFRESH:
                // do nothing because only SwipeRefreshLayout has been shown
                break;
            default:
                // disable nested scrolling if the first and last child items
                // are fully visible or no child items exists
                recyclerView.post(() -> {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager)
                            recyclerView.getLayoutManager();
                    int firstCompletelyVisibleItemPosition =
                            linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    int itemCount = linearLayoutManager.getItemCount();

                    boolean isChildViewsFullyVisible = firstCompletelyVisibleItemPosition == 0
                            && linearLayoutManager.findLastCompletelyVisibleItemPosition()
                            == itemCount - 1;
                    recyclerView.setNestedScrollingEnabled(!(isChildViewsFullyVisible
                            || itemCount == 0));
                });
        }
    }
}
