package cl.monsoon.s1next.view.adapter.delegate;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;

import java.util.List;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Post;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.databinding.ItemPostBinding;
import cl.monsoon.s1next.viewmodel.PostViewModel;
import cl.monsoon.s1next.widget.EventBus;

public final class PostAdapterDelegate extends AbsAdapterDelegate<List<Object>> {

    @Inject
    EventBus mEventBus;

    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    private final LayoutInflater mLayoutInflater;

    private final DrawableRequestBuilder<String> mAvatarRequestBuilder;

    public PostAdapterDelegate(Activity activity, int viewType) {
        super(viewType);

        App.getAppComponent(activity).inject(this);
        mLayoutInflater = activity.getLayoutInflater();
        // loading avatars is prior to images in replies
        mAvatarRequestBuilder = Glide.with(activity)
                .from(String.class)
                .error(R.drawable.ic_avatar_placeholder)
                .priority(Priority.HIGH)
                .transform(new CenterCrop(Glide.get(activity).getBitmapPool()));
    }

    @Override
    public boolean isForViewType(@NonNull List<Object> objectList, int i) {
        return objectList.get(i) instanceof Post;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        ItemPostBinding itemPostBinding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_post, viewGroup, false);
        itemPostBinding.setEventBus(mEventBus);
        itemPostBinding.setDownloadPreferencesManager(mDownloadPreferencesManager);
        itemPostBinding.setDrawableRequestBuilder(mAvatarRequestBuilder);
        itemPostBinding.setPostViewModel(new PostViewModel());

        return new ItemViewBindingHolder(itemPostBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull List<Object> objectList, int i, @NonNull RecyclerView.ViewHolder viewHolder) {
        ItemPostBinding binding = ((ItemViewBindingHolder) viewHolder).itemPostBinding;
        binding.getPostViewModel().post.set((Post) objectList.get(i));
        binding.executePendingBindings();
    }

    private static final class ItemViewBindingHolder extends RecyclerView.ViewHolder {

        private final ItemPostBinding itemPostBinding;

        public ItemViewBindingHolder(ItemPostBinding itemPostBinding) {
            super(itemPostBinding.getRoot());

            this.itemPostBinding = itemPostBinding;
        }
    }
}
