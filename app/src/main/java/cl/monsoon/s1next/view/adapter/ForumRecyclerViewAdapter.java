package cl.monsoon.s1next.view.adapter;

import android.app.Activity;

import cl.monsoon.s1next.data.api.model.Forum;
import cl.monsoon.s1next.view.adapter.delegate.ForumAdapterDelegate;

public final class ForumRecyclerViewAdapter extends BaseRecyclerViewAdapter<Forum> {

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
