package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.ThreadAdapterDelegate;

public final class ThreadRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public ThreadRecyclerViewAdapter(Activity activity, String forumId) {
        super(activity);

        addAdapterDelegate(new ThreadAdapterDelegate(activity, forumId));
    }

}
