package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Search;
import me.ykrank.s1next.databinding.ItemSearchBinding;
import me.ykrank.s1next.viewmodel.SearchViewModel;

public final class SearchAdapterDelegate extends BaseAdapterDelegate<Search, SearchAdapterDelegate.BindingViewHolder> {

    public SearchAdapterDelegate(Context context, int viewType) {
        super(context, viewType);
    }

    @NonNull
    @Override
    protected Class<Search> getTClass() {
        return Search.class;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemSearchBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_search, parent, false);
        binding.setModel(new SearchViewModel());

        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolderData(Search search, int position, @NonNull BindingViewHolder holder) {
        ItemSearchBinding binding = holder.binding;
        binding.getModel().search.set(search);
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
