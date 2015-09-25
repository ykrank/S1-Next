package cl.monsoon.s1next.view.adapter;

import android.app.Activity;

import com.google.common.base.Preconditions;

import cl.monsoon.s1next.data.api.model.Post;
import cl.monsoon.s1next.view.adapter.delegate.PostAdapterDelegate;
import cl.monsoon.s1next.view.adapter.delegate.PostFooterProgressAdapterDelegate;
import cl.monsoon.s1next.view.adapter.item.FooterProgressItem;

/**
 * This {@link android.support.v7.widget.RecyclerView.Adapter}
 * has another item type {@link #TYPE_POST_FOOTER_PROGRESS}
 * in order to implement pull up to refresh.
 */
public final class PostListRecyclerViewAdapter extends BaseRecyclerViewAdapter<Post> {

    private static final int VIEW_TYPE_POST = 1;
    private static final int TYPE_POST_FOOTER_PROGRESS = 2;

    public PostListRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new PostAdapterDelegate(activity, VIEW_TYPE_POST));
        addAdapterDelegate(new PostFooterProgressAdapterDelegate(activity, TYPE_POST_FOOTER_PROGRESS));

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (getItemViewTypeFromDelegatesManager(position) == VIEW_TYPE_POST) {
            return Long.parseLong(((Post) getItem(position)).getId());
        } else if (getItemViewTypeFromDelegatesManager(position) == TYPE_POST_FOOTER_PROGRESS) {
            return Integer.MIN_VALUE + 1;
        } else {
            return super.getItemId(position);
        }
    }

    public void showFooterProgress() {
        int position = getItemCount() - 1;
        Preconditions.checkState(getItem(position) != null);
        addItem(new FooterProgressItem());
        notifyItemInserted(position + 1);
    }

    public void hideFooterProgress() {
        int position = getItemCount() - 1;
        Preconditions.checkState(getItem(position) instanceof FooterProgressItem);
        removeItem(position);
        notifyItemRemoved(position);
    }
}
