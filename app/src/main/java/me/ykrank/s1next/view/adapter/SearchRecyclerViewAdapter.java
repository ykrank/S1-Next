package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.SearchForumAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.SearchUserAdapterDelegate;

public final class SearchRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public SearchRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new SearchForumAdapterDelegate(activity));
        addAdapterDelegate(new SearchUserAdapterDelegate(activity));
    }
}
