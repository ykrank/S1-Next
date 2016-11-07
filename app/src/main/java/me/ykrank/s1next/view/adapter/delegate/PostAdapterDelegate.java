package me.ykrank.s1next.view.adapter.delegate;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.databinding.ItemPostBinding;
import me.ykrank.s1next.viewmodel.PostViewModel;
import me.ykrank.s1next.widget.EventBus;

public final class PostAdapterDelegate extends BaseAdapterDelegate<Post, PostAdapterDelegate.ItemViewBindingHolder> {

    @Inject
    EventBus mEventBus;

    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    private final DrawableRequestBuilder<String> mAvatarRequestBuilder;

    public PostAdapterDelegate(Activity activity) {
        super(activity);

        App.getPrefComponent(activity).inject(this);
        // loading avatars is prior to images in replies
        mAvatarRequestBuilder = Glide.with(activity)
                .from(String.class)
                .error(R.drawable.ic_avatar_placeholder)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(new CenterCrop(Glide.get(activity).getBitmapPool()));
    }

    @NonNull
    @Override
    protected Class<Post> getTClass() {
        return Post.class;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemPostBinding itemPostBinding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_post, parent, false);
        itemPostBinding.setEventBus(mEventBus);
        itemPostBinding.setDownloadPreferencesManager(mDownloadPreferencesManager);
        itemPostBinding.setDrawableRequestBuilder(mAvatarRequestBuilder);
        itemPostBinding.setPostViewModel(new PostViewModel());
        itemPostBinding.setSelectable(true);

        return new ItemViewBindingHolder(itemPostBinding);
    }

    @Override
    public void onBindViewHolderData(Post post, int position, @NonNull ItemViewBindingHolder holder) {
        ItemPostBinding binding = holder.itemPostBinding;
        binding.getPostViewModel().post.set(post);
        binding.executePendingBindings();
    }

    // Bug workaround for losing text selection ability, see:
    // https://code.google.com/p/android/issues/detail?id=208169
    @Override
    protected void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ItemPostBinding binding = ((ItemViewBindingHolder)holder).itemPostBinding;
        binding.authorName.setEnabled(false);
        binding.tvFloor.setEnabled(false);
        binding.tvReply.setEnabled(false);
        binding.authorName.setEnabled(true);
        binding.tvFloor.setEnabled(true);
        binding.tvReply.setEnabled(true);
    }

    static final class ItemViewBindingHolder extends RecyclerView.ViewHolder {

        private final ItemPostBinding itemPostBinding;

        public ItemViewBindingHolder(ItemPostBinding itemPostBinding) {
            super(itemPostBinding.getRoot());

            this.itemPostBinding = itemPostBinding;
        }
    }
}
