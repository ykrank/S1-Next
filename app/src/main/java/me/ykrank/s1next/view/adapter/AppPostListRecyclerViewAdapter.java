package me.ykrank.s1next.view.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;

import me.ykrank.s1next.data.api.app.model.AppThread;
import me.ykrank.s1next.view.adapter.delegate.AppPostAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.FooterProgressAdapterDelegate;

/**
 * This {@link android.support.v7.widget.RecyclerView.Adapter}
 * has another item type {@link FooterProgressAdapterDelegate}
 * in order to implement pull up to refresh.
 */
public final class AppPostListRecyclerViewAdapter extends BaseRecyclerViewAdapter {
    @NonNull
    private AppPostAdapterDelegate postAdapterDelegate;

    public AppPostListRecyclerViewAdapter(Activity activity) {
        super(activity);

        postAdapterDelegate = new AppPostAdapterDelegate(activity);
        addAdapterDelegate(postAdapterDelegate);
        addAdapterDelegate(new FooterProgressAdapterDelegate(activity));
    }

    public void setThreadInfo(@NonNull AppThread threadInfo) {
        postAdapterDelegate.setThreadInfo(threadInfo);
    }
}
