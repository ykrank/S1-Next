package me.ykrank.s1next.view.adapter.delegate;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.app.model.AppPost;
import me.ykrank.s1next.data.api.app.model.AppThread;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.databinding.ItemAppPostBinding;
import me.ykrank.s1next.view.adapter.simple.SimpleRecycleViewHolder;
import me.ykrank.s1next.viewmodel.AppPostViewModel;
import me.ykrank.s1next.widget.RxBus;
import me.ykrank.s1next.widget.span.PostMovementMethod;

public final class AppPostAdapterDelegate extends BaseAdapterDelegate<AppPost, SimpleRecycleViewHolder<ItemAppPostBinding>> {

    @Inject
    RxBus mRxBus;
    @Inject
    User mUser;
    @Inject
    GeneralPreferencesManager mGeneralPreferencesManager;

    @Nullable
    private AppThread threadInfo;

    public AppPostAdapterDelegate(Activity activity) {
        super(activity);

        App.getAppComponent().inject(this);
    }

    private static void setTextSelectable(ItemAppPostBinding binding, boolean selectable) {
        binding.authorName.setTextIsSelectable(selectable);
        binding.tvFloor.setTextIsSelectable(selectable);
        binding.tvReply.setTextIsSelectable(selectable);
        binding.authorName.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvFloor.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvReply.setMovementMethod(PostMovementMethod.getInstance());
        binding.tvFloor.setLongClickable(false);
    }

    @NonNull
    @Override
    protected Class<AppPost> getTClass() {
        return AppPost.class;
    }

    @Override
    public boolean isForViewType(@NonNull List<Object> items, int position) {
        return super.isForViewType(items, position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemAppPostBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_app_post, parent, false);
        binding.setPostViewModel(new AppPostViewModel(mRxBus, mUser));

        //If setTextIsSelectable, then should reset movement
        boolean selectable = mGeneralPreferencesManager.isPostSelectable();
        setTextSelectable(binding, selectable);

        return new SimpleRecycleViewHolder<ItemAppPostBinding>(binding);
    }

    @Override
    public void onBindViewHolderData(AppPost post, int position, @NonNull SimpleRecycleViewHolder<ItemAppPostBinding> holder, @NonNull List<Object> payloads) {
        ItemAppPostBinding binding = holder.getBinding();

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
            ItemAppPostBinding binding = ((SimpleRecycleViewHolder<ItemAppPostBinding>) holder).getBinding();
            binding.authorName.setEnabled(false);
            binding.tvFloor.setEnabled(false);
            binding.tvReply.setEnabled(false);
            binding.authorName.setEnabled(true);
            binding.tvFloor.setEnabled(true);
            binding.tvReply.setEnabled(true);
        }
    }

    public void setThreadInfo(@NonNull AppThread threadInfo) {
        this.threadInfo = threadInfo;
    }
}
