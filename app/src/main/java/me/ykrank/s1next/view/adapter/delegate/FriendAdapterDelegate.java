package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import me.ykrank.s1next.data.api.model.Friend;
import me.ykrank.s1next.databinding.ItemFriendBinding;
import me.ykrank.s1next.viewmodel.FriendViewModel;

/**
 * Created by ykrank on 2017/1/16.
 */

public class FriendAdapterDelegate extends BaseAdapterDelegate<Friend, FriendAdapterDelegate.BindingViewHolder> {

    public FriendAdapterDelegate(Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected Class<Friend> getTClass() {
        return Friend.class;
    }

    @Override
    public void onBindViewHolderData(Friend friend, int position, @NonNull BindingViewHolder holder) {
        ItemFriendBinding binding = holder.binding;
        binding.getData().friend.set(friend);
        binding.executePendingBindings();
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemFriendBinding binding = ItemFriendBinding.inflate(mLayoutInflater, parent, false);
        binding.setData(new FriendViewModel());
        return new BindingViewHolder(binding);
    }

    static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemFriendBinding binding;

        public BindingViewHolder(ItemFriendBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
