package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Forum;
import me.ykrank.s1next.databinding.ItemForumBinding;
import me.ykrank.s1next.viewmodel.ForumViewModel;

public final class ForumAdapterDelegate extends BaseAdapterDelegate<Forum, ForumAdapterDelegate.BindingViewHolder> {
    private final int mGentleAccentColor;

    public ForumAdapterDelegate(Context context, int viewType) {
        super(context, viewType);

        mGentleAccentColor = App.getAppComponent(context).getThemeManager().getGentleAccentColor();
    }

    @NonNull
    @Override
    protected Class<Forum> getTClass() {
        return Forum.class;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemForumBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_forum, parent, false);
        binding.setGentleAccentColor(mGentleAccentColor);
        binding.setForumViewModel(new ForumViewModel());

        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolderData(Forum forum, int position, @NonNull BindingViewHolder holder) {
        ItemForumBinding binding = holder.itemForumBinding;
        binding.getForumViewModel().forum.set(forum);
        binding.executePendingBindings();
    }

    static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemForumBinding itemForumBinding;

        public BindingViewHolder(ItemForumBinding itemForumBinding) {
            super(itemForumBinding.getRoot());

            this.itemForumBinding = itemForumBinding;
        }
    }
}
