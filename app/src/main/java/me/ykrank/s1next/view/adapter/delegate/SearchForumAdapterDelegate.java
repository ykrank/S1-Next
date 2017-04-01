package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.search.ForumSearchResult;
import me.ykrank.s1next.databinding.ItemSearchForumBinding;
import me.ykrank.s1next.view.adapter.simple.SimpleRecycleViewHolder;

public final class SearchForumAdapterDelegate extends BaseAdapterDelegate<ForumSearchResult, SimpleRecycleViewHolder<ItemSearchForumBinding>> {

    public SearchForumAdapterDelegate(Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected Class<ForumSearchResult> getTClass() {
        return ForumSearchResult.class;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemSearchForumBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_search_forum, parent, false);

        return new SimpleRecycleViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolderData(ForumSearchResult forumSearchResult, int position, @NonNull SimpleRecycleViewHolder<ItemSearchForumBinding> holder) {
        ItemSearchForumBinding binding = holder.getBinding();
        binding.setModel(forumSearchResult);
        binding.executePendingBindings();
    }
}
