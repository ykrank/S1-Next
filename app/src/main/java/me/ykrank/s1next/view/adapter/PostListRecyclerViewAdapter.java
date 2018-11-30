package me.ykrank.s1next.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.ykrank.androidtools.ui.adapter.delegate.FooterProgressAdapterDelegate;

import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.api.model.Vote;
import me.ykrank.s1next.view.adapter.delegate.PostAdapterDelegate;

/**
 * This {@link android.support.v7.widget.RecyclerView.Adapter}
 * has another item type {@link FooterProgressAdapterDelegate}
 * in order to implement pull up to refresh.
 */
public final class PostListRecyclerViewAdapter extends BaseRecyclerViewAdapter {
    @NonNull
    private PostAdapterDelegate postAdapterDelegate;

    public PostListRecyclerViewAdapter(Fragment fragment, @NonNull Context context) {
        super(context);

        postAdapterDelegate = new PostAdapterDelegate(fragment, context);
        addAdapterDelegate(postAdapterDelegate);
    }

    public void setThreadInfo(@NonNull Thread threadInfo, int pageNum) {
        postAdapterDelegate.setThreadInfo(threadInfo, pageNum);
    }

    public void setVoteInfo(@Nullable Vote voteInfo) {
        postAdapterDelegate.setVoteInfo(voteInfo);
    }
}
