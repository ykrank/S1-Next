package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.SearchAdapterDelegate;

public final class SearchRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    private static final int VIEW_TYPE_SEARCH = 1;

    public SearchRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new SearchAdapterDelegate(activity, VIEW_TYPE_SEARCH));

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (getItemViewTypeFromDelegatesManager(position) == VIEW_TYPE_SEARCH) {
            return position;
        } else {
            return super.getItemId(position);
        }
    }
}
