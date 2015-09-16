package cl.monsoon.s1next.view.adapter.delegate;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;

import java.util.List;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Forum;
import cl.monsoon.s1next.databinding.ItemForumBinding;
import cl.monsoon.s1next.viewmodel.ForumViewModel;

public final class ForumAdapterDelegate extends AbsAdapterDelegate<List<Object>> {

    private final LayoutInflater mLayoutInflater;

    private final int mGentleAccentColor;

    public ForumAdapterDelegate(Activity activity, int viewType) {
        super(viewType);

        mLayoutInflater = activity.getLayoutInflater();
        mGentleAccentColor = App.getAppComponent(activity).getThemeManager().getGentleAccentColor();
    }

    @Override
    public boolean isForViewType(@NonNull List<Object> objectList, int i) {
        return objectList.get(i) instanceof Forum;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        ItemForumBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_forum, viewGroup, false);
        binding.setGentleAccentColor(mGentleAccentColor);
        binding.setForumViewModel(new ForumViewModel());

        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull List<Object> objectList, int i, @NonNull RecyclerView.ViewHolder viewHolder) {
        ItemForumBinding binding = ((BindingViewHolder) viewHolder).itemForumBinding;
        binding.getForumViewModel().forum.set((Forum) objectList.get(i));
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
