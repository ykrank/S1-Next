package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.SearchAdapterDelegate;

public final class SearchRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public SearchRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new SearchAdapterDelegate(activity));
    }
}
