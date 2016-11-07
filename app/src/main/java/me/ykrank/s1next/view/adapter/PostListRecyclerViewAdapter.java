package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import com.google.common.base.Preconditions;

import me.ykrank.s1next.view.adapter.delegate.PostAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.PostFooterProgressAdapterDelegate;
import me.ykrank.s1next.view.adapter.item.FooterProgressItem;

/**
 * This {@link android.support.v7.widget.RecyclerView.Adapter}
 * has another item type {@link PostFooterProgressAdapterDelegate}
 * in order to implement pull up to refresh.
 */
public final class PostListRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public PostListRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new PostAdapterDelegate(activity));
        addAdapterDelegate(new PostFooterProgressAdapterDelegate(activity));

        setHasStableIds(true);
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
