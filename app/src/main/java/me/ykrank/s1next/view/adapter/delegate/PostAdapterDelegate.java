package me.ykrank.s1next.view.adapter.delegate;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.ViewGroup;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.databinding.ItemPostBinding;
import me.ykrank.s1next.viewmodel.PostViewModel;
import me.ykrank.s1next.widget.EventBus;
import me.ykrank.s1next.widget.span.PostMovementMethod;

public final class PostAdapterDelegate extends BaseAdapterDelegate<Post, PostAdapterDelegate.ItemViewBindingHolder> {

    @Inject
    EventBus mEventBus;
    @Inject
    User mUser;
    @Inject
    GeneralPreferencesManager mGeneralPreferencesManager;

    @Nullable
    private Thread threadInfo;

    public PostAdapterDelegate(Activity activity) {
        super(activity);

        App.getPrefComponent().inject(this);
    }

    private static void setTextSelectable(ItemPostBinding binding, boolean selectable) {
        binding.tvFloor.setTextIsSelectable(selectable);
        binding.tvReply.setTextIsSelectable(selectable);
        binding.tvFloor.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvReply.setMovementMethod(PostMovementMethod.getInstance());
    }

    @NonNull
    @Override
    protected Class<Post> getTClass() {
        return Post.class;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemPostBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_post, parent, false);
        binding.setPostViewModel(new PostViewModel(mEventBus, mUser));

        //If setTextIsSelectable, then should reset movement
        boolean selectable = mGeneralPreferencesManager.isPostSelectable();
        setTextSelectable(binding, selectable);

        return new ItemViewBindingHolder(binding);
    }

    @Override
    public void onBindViewHolderData(Post post, int position, @NonNull ItemViewBindingHolder holder) {
        ItemPostBinding binding = holder.itemPostBinding;

        boolean selectable = mGeneralPreferencesManager.isPostSelectable();
        if (selectable != binding.tvReply.isTextSelectable()) {
            setTextSelectable(binding, selectable);
        }

        binding.getPostViewModel().thread.set(threadInfo);
        binding.getPostViewModel().post.set(post);
        binding.executePendingBindings();
    }

    // Bug workaround for losing text selection ability, see:
    // https://code.google.com/p/android/issues/detail?id=208169
    @Override
    protected void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (mGeneralPreferencesManager.isPostSelectable()) {
            ItemPostBinding binding = ((ItemViewBindingHolder) holder).itemPostBinding;
            binding.authorName.setEnabled(false);
            binding.tvFloor.setEnabled(false);
            binding.tvReply.setEnabled(false);
            binding.authorName.setEnabled(true);
            binding.tvFloor.setEnabled(true);
            binding.tvReply.setEnabled(true);
        }
    }

    public void setThreadInfo(@NonNull Thread threadInfo) {
        this.threadInfo = threadInfo;
    }

    static final class ItemViewBindingHolder extends RecyclerView.ViewHolder {

        private final ItemPostBinding itemPostBinding;

        public ItemViewBindingHolder(ItemPostBinding itemPostBinding) {
            super(itemPostBinding.getRoot());

            this.itemPostBinding = itemPostBinding;
        }
    }
}
