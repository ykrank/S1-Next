package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.data.api.model.Forum;
import me.ykrank.s1next.view.adapter.delegate.ForumAdapterDelegate;

public final class ForumRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    private static final int VIEW_TYPE_FORUM = 1;

    public ForumRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new ForumAdapterDelegate(activity, VIEW_TYPE_FORUM));

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (getItemViewTypeFromDelegatesManager(position) == VIEW_TYPE_FORUM) {
            return Long.parseLong(((Forum) getItem(position)).getId());
        } else {
            return super.getItemId(position);
        }
    }
}
