package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import me.ykrank.s1next.data.api.model.HomeReply;
import me.ykrank.s1next.databinding.ItemHomeReplyItemBinding;
import me.ykrank.s1next.viewmodel.HomeReplyItemViewModel;

/**
 * Created by ykrank on 2017/2/4.
 */

public class HomeReplyItemAdapterDelegate extends BaseAdapterDelegate<HomeReply, HomeReplyItemAdapterDelegate.BindingViewHolder> {

    public HomeReplyItemAdapterDelegate(Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected Class<HomeReply> getTClass() {
        return HomeReply.class;
    }

    @Override
    public void onBindViewHolderData(HomeReply thread, int position, @NonNull BindingViewHolder holder, @NonNull List<Object> payloads) {
        ItemHomeReplyItemBinding binding = holder.binding;
        binding.getModel().reply.set(thread);
        binding.executePendingBindings();
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemHomeReplyItemBinding binding = ItemHomeReplyItemBinding.inflate(mLayoutInflater, parent, false);
        binding.setModel(new HomeReplyItemViewModel());
        return new HomeReplyItemAdapterDelegate.BindingViewHolder(binding);
    }

    static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemHomeReplyItemBinding binding;

        public BindingViewHolder(ItemHomeReplyItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
