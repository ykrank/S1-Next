package me.ykrank.s1next.view.adapter;

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

    public PostListRecyclerViewAdapter(Fragment fragment) {
        super(fragment.getContext());

        postAdapterDelegate = new PostAdapterDelegate(fragment);
        addAdapterDelegate(postAdapterDelegate);
        addAdapterDelegate(new FooterProgressAdapterDelegate(fragment.getContext()));
    }

    public void setThreadInfo(@NonNull Thread threadInfo) {
        postAdapterDelegate.setThreadInfo(threadInfo);
    }

    public void setVoteInfo(@Nullable Vote voteInfo) {
        postAdapterDelegate.setVoteInfo(voteInfo);
    }
}
