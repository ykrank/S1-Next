package me.ykrank.s1next.view.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ykrank.androidtools.ui.adapter.delegate.FooterProgressAdapterDelegate;

import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.api.model.Vote;
import me.ykrank.s1next.view.adapter.delegate.PostAdapterDelegate;

/**
 * This {@link RecyclerView.Adapter}
 * has another item type {@link FooterProgressAdapterDelegate}
 * in order to implement pull up to refresh.
 */
public final class PostListRecyclerViewAdapter extends BaseRecyclerViewAdapter {
    @NonNull
    private PostAdapterDelegate postAdapterDelegate;

    public PostListRecyclerViewAdapter(Fragment fragment, @NonNull Context context) {
        super(context, true);

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
