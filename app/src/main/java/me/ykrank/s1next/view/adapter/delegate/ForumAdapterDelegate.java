package me.ykrank.s1next.view.adapter.delegate;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;

import java.util.List;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Forum;
import me.ykrank.s1next.databinding.ItemForumBinding;
import me.ykrank.s1next.viewmodel.ForumViewModel;

public final class ForumAdapterDelegate extends AbsAdapterDelegate<List<Object>> {

    private final LayoutInflater mLayoutInflater;

    private final int mGentleAccentColor;

    public ForumAdapterDelegate(Activity activity, int viewType) {
        super(viewType);

        mLayoutInflater = activity.getLayoutInflater();
        mGentleAccentColor = App.getAppComponent(activity).getThemeManager().getGentleAccentColor();
    }

    @Override
    public boolean isForViewType(@NonNull List<Object> items, int position) {
        return items.get(position) instanceof Forum;
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
    public void onBindViewHolder(@NonNull List<Object> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        ItemForumBinding binding = ((BindingViewHolder) holder).itemForumBinding;
        binding.getForumViewModel().forum.set((Forum) items.get(position));
        binding.executePendingBindings();
    }

    private static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemForumBinding itemForumBinding;

        public BindingViewHolder(ItemForumBinding itemForumBinding) {
            super(itemForumBinding.getRoot());

            this.itemForumBinding = itemForumBinding;
        }
    }
}
