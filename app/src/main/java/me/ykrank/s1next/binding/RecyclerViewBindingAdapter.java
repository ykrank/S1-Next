package me.ykrank.s1next.binding;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter;

public final class RecyclerViewBindingAdapter {

    private RecyclerViewBindingAdapter() {}

    @BindingAdapter("loadingFirstTime")
    public static void setHasProgress(RecyclerView recyclerView, Boolean oldIsLoadingFirstTime, Boolean newIsLoadingFirstTime) {
        if (newIsLoadingFirstTime != oldIsLoadingFirstTime) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter instanceof BaseRecyclerViewAdapter){
                BaseRecyclerViewAdapter baseRecyclerViewAdapter = (BaseRecyclerViewAdapter)adapter;
                baseRecyclerViewAdapter.setHasProgress(newIsLoadingFirstTime);
            }
        }
    }
}
