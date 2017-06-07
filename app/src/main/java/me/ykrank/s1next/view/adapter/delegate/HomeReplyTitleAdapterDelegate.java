package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import me.ykrank.s1next.data.api.model.HomeThread;
import me.ykrank.s1next.databinding.ItemHomeReplyTitleBinding;
import me.ykrank.s1next.viewmodel.HomeReplyTitleViewModel;

/**
 * Created by ykrank on 2017/2/4.
 */

public class HomeReplyTitleAdapterDelegate extends BaseAdapterDelegate<HomeThread, HomeReplyTitleAdapterDelegate.BindingViewHolder> {

    public HomeReplyTitleAdapterDelegate(Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected Class<HomeThread> getTClass() {
        return HomeThread.class;
    }

    @Override
    public void onBindViewHolderData(HomeThread thread, int position, @NonNull BindingViewHolder holder, @NonNull List<Object> payloads) {
        ItemHomeReplyTitleBinding binding = holder.binding;
        binding.getModel().thread.set(thread);
        binding.executePendingBindings();
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemHomeReplyTitleBinding binding = ItemHomeReplyTitleBinding.inflate(mLayoutInflater, parent, false);
        binding.setModel(new HomeReplyTitleViewModel());
        return new HomeReplyTitleAdapterDelegate.BindingViewHolder(binding);
    }

    static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemHomeReplyTitleBinding binding;

        public BindingViewHolder(ItemHomeReplyTitleBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
