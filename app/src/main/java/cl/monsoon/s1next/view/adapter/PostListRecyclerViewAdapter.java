package cl.monsoon.s1next.view.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.common.base.Preconditions;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Post;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.databinding.ItemPostBinding;
import cl.monsoon.s1next.viewmodel.PostViewModel;
import cl.monsoon.s1next.widget.EventBus;

/**
 * This {@link android.support.v7.widget.RecyclerView.Adapter}
 * has another item type {@link #TYPE_FOOTER_PROGRESS}
 * in order to implement pull up to refresh.
 */
public final class PostListRecyclerViewAdapter extends BaseRecyclerViewAdapter<Post, RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER_PROGRESS = Integer.MIN_VALUE;

    @Inject
    EventBus mEventBus;

    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    private final LayoutInflater mLayoutInflater;

    private final DrawableRequestBuilder<String> mAvatarRequestBuilder;

    public PostListRecyclerViewAdapter(Activity activity) {
        App.getAppComponent(activity).inject(this);
        mLayoutInflater = activity.getLayoutInflater();
        // loading avatars is prior to images in replies
        mAvatarRequestBuilder = Glide.with(activity)
                .from(String.class)
                .error(R.drawable.ic_avatar_placeholder)
                .priority(Priority.HIGH)
                .transform(new CenterCrop(Glide.get(activity).getBitmapPool()));

        setHasStableIds(true);
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterProgress(position)) {
            return TYPE_FOOTER_PROGRESS;
        }

        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER_PROGRESS) {
            return new FooterProgressViewHolder(mLayoutInflater.inflate(
                    R.layout.item_post_footer_progress, parent, false));
        }

        ItemPostBinding itemPostBinding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_post, parent, false);
        itemPostBinding.setEventBus(mEventBus);
        itemPostBinding.setDownloadPreferencesManager(mDownloadPreferencesManager);
        itemPostBinding.setDrawableRequestBuilder(mAvatarRequestBuilder);
        itemPostBinding.setPostViewModel(new PostViewModel());

        return new ItemViewBindingHolder(itemPostBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isFooterProgress(position)) {
            return;
        }

        ItemViewBindingHolder itemViewBindingHolder = (ItemViewBindingHolder) holder;
        itemViewBindingHolder.itemPostBinding.getPostViewModel().post.set(getItem(position));
        itemViewBindingHolder.itemPostBinding.executePendingBindings();
    }

    @Override
    public long getItemId(int position) {
        if (isFooterProgress(position)) {
            return Integer.MIN_VALUE;
        }

        return Long.parseLong(getItem(position).getCount());
    }

    private boolean isFooterProgress(int position) {
        return position == getItemCount() - 1 && getItem(position) == null;
    }

    public void showFooterProgress() {
        int position = getItemCount() - 1;
        // use null as ProgressBar's item
        Preconditions.checkState(getItem(position) != null);
        addItem(null);
        notifyItemInserted(position + 1);
    }

    public void hideFooterProgress() {
        int position = getItemCount() - 1;
        Preconditions.checkState(getItem(position) == null);
        removeItem(position);
        notifyItemRemoved(position);
    }

    private static class ItemViewBindingHolder extends RecyclerView.ViewHolder {

        private final ItemPostBinding itemPostBinding;

        public ItemViewBindingHolder(ItemPostBinding itemPostBinding) {
            super(itemPostBinding.getRoot());

            this.itemPostBinding = itemPostBinding;
        }
    }

    private static class FooterProgressViewHolder extends RecyclerView.ViewHolder {

        public FooterProgressViewHolder(View itemView) {
            super(itemView);
        }
    }
}
