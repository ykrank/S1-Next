package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.ForumSearchResult;
import me.ykrank.s1next.databinding.ItemSearchBinding;

public final class SearchAdapterDelegate extends BaseAdapterDelegate<ForumSearchResult, SearchAdapterDelegate.BindingViewHolder> {

    public SearchAdapterDelegate(Context context) {
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
        ItemSearchBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_search, parent, false);

        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolderData(ForumSearchResult forumSearchResult, int position, @NonNull BindingViewHolder holder) {
        ItemSearchBinding binding = holder.binding;
        binding.setModel(forumSearchResult);
        binding.executePendingBindings();
    }

    static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemSearchBinding binding;

        public BindingViewHolder(ItemSearchBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
