package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.search.UserSearchResult;
import me.ykrank.s1next.databinding.ItemSearchUserBinding;
import me.ykrank.s1next.view.adapter.simple.SimpleRecycleViewHolder;
import me.ykrank.s1next.viewmodel.SearchUserViewModel;

public final class SearchUserAdapterDelegate extends BaseAdapterDelegate<UserSearchResult, SimpleRecycleViewHolder<ItemSearchUserBinding>> {

    public SearchUserAdapterDelegate(Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected Class<UserSearchResult> getTClass() {
        return UserSearchResult.class;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemSearchUserBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_search_user, parent, false);
        binding.setModel(new SearchUserViewModel());
        return new SimpleRecycleViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolderData(UserSearchResult userSearchResult, int position, @NonNull SimpleRecycleViewHolder<ItemSearchUserBinding> holder, @NonNull List<Object> payloads) {
        ItemSearchUserBinding binding = holder.getBinding();
        binding.getModel().search.set(userSearchResult);
        binding.executePendingBindings();
    }
}
