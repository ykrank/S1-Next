package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.FooterProgressAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.PostAdapterDelegate;

/**
 * This {@link android.support.v7.widget.RecyclerView.Adapter}
 * has another item type {@link FooterProgressAdapterDelegate}
 * in order to implement pull up to refresh.
 */
public final class PostListRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public PostListRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new PostAdapterDelegate(activity));
        addAdapterDelegate(new FooterProgressAdapterDelegate(activity));
    }
}
