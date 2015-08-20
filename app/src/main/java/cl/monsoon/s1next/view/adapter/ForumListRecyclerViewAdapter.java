package cl.monsoon.s1next.view.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Forum;
import cl.monsoon.s1next.databinding.ItemForumBinding;
import cl.monsoon.s1next.viewmodel.ForumViewModel;

public final class ForumListRecyclerViewAdapter
        extends BaseRecyclerViewAdapter<Forum, ForumListRecyclerViewAdapter.BindingViewHolder> {

    private final LayoutInflater mLayoutInflater;

    private final int mGentleAccentColor;

    public ForumListRecyclerViewAdapter(Activity activity) {
        mLayoutInflater = activity.getLayoutInflater();
        mGentleAccentColor = App.getAppComponent(activity).getThemeManager().getGentleAccentColor();

        setHasStableIds(true);
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemForumBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_forum, parent, false);
        BindingViewHolder holder = new BindingViewHolder(binding);
        holder.itemForumBinding.setGentleAccentColor(mGentleAccentColor);
        holder.itemForumBinding.setForumViewModel(new ForumViewModel());

        return holder;
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        holder.itemForumBinding.getForumViewModel().forum.set(getItem(position));
        holder.itemForumBinding.executePendingBindings();
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(getItem(position).getId());
    }

    public final static class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemForumBinding itemForumBinding;

        public BindingViewHolder(ItemForumBinding itemForumBinding) {
            super(itemForumBinding.getRoot());

            this.itemForumBinding = itemForumBinding;
        }
    }
}
