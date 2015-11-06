package cl.monsoon.s1next.binding;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import cl.monsoon.s1next.view.adapter.BaseRecyclerViewAdapter;

public final class RecyclerViewBindingAdapter {

    private RecyclerViewBindingAdapter() {}

    @BindingAdapter("loadingFirstTime")
    public static void setHasProgress(RecyclerView recyclerView, Boolean oldIsLoadingFirstTime, Boolean newIsLoadingFirstTime) {
        if (newIsLoadingFirstTime != oldIsLoadingFirstTime) {
            BaseRecyclerViewAdapter baseRecyclerViewAdapter = (BaseRecyclerViewAdapter)
                    recyclerView.getAdapter();
            baseRecyclerViewAdapter.setHasProgress(newIsLoadingFirstTime);
            baseRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
