package cl.monsoon.s1next.view.adapter;

import android.app.Activity;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Forum;
import cl.monsoon.s1next.databinding.ItemForumBinding;
import cl.monsoon.s1next.util.ViewUtil;
import cl.monsoon.s1next.viewmodel.ForumViewModel;

public final class ForumListRecyclerViewAdapter extends BaseRecyclerViewAdapter<Forum, ForumListRecyclerViewAdapter.BindingViewHolder> {

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

        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        holder.itemForumBinding.setForumViewModel(new ForumViewModel(getItem(position)));
        holder.itemForumBinding.executePendingBindings();
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mList.get(position).getId());
    }

    @BindingAdapter({"forum", "gentleAccentColor"})
    public static void showForum(TextView textView, Forum forum, int gentleAccentColor) {
        textView.setText(forum.getName());
        // add today's posts count to each forum
        if (forum.getTodayPosts() != 0) {
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView, String.valueOf(forum.getTodayPosts()),
                    gentleAccentColor);
        }
    }

    public static class BindingViewHolder extends RecyclerView.ViewHolder {

        private ItemForumBinding itemForumBinding;

        public BindingViewHolder(ItemForumBinding itemForumBinding) {
            super(itemForumBinding.getRoot());

            this.itemForumBinding = itemForumBinding;
        }
    }
}
