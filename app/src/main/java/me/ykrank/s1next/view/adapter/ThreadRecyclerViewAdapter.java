package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.view.adapter.delegate.ThreadAdapterDelegate;

public final class ThreadRecyclerViewAdapter extends BaseRecyclerViewAdapter<Thread> {

    private static final int VIEW_TYPE_THREAD = 1;

    public ThreadRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new ThreadAdapterDelegate(activity, VIEW_TYPE_THREAD));

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (getItemViewTypeFromDelegatesManager(position) == VIEW_TYPE_THREAD) {
            return Long.parseLong(((Thread) getItem(position)).getId());
        } else {
            return super.getItemId(position);
        }
    }
}
