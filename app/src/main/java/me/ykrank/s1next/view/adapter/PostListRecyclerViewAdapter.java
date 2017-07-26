package me.ykrank.s1next.view.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;

import me.ykrank.s1next.data.api.app.model.AppThread;
import me.ykrank.s1next.view.adapter.delegate.FooterProgressAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.PostAdapterDelegate;

/**
 * This {@link android.support.v7.widget.RecyclerView.Adapter}
 * has another item type {@link FooterProgressAdapterDelegate}
 * in order to implement pull up to refresh.
 */
public final class PostListRecyclerViewAdapter extends BaseRecyclerViewAdapter {
    @NonNull
    private PostAdapterDelegate postAdapterDelegate;

    public PostListRecyclerViewAdapter(Activity activity) {
        super(activity);

        postAdapterDelegate = new PostAdapterDelegate(activity);
        addAdapterDelegate(postAdapterDelegate);
        addAdapterDelegate(new FooterProgressAdapterDelegate(activity));
    }

    public void setThreadInfo(@NonNull AppThread threadInfo) {
        postAdapterDelegate.setThreadInfo(threadInfo);
    }
}
