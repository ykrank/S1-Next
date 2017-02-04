package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import me.ykrank.s1next.data.api.model.HomeThread;
import me.ykrank.s1next.databinding.ItemHomeThreadBinding;
import me.ykrank.s1next.viewmodel.HomeThreadViewModel;

/**
 * Created by ykrank on 2017/2/4.
 */

public class HomeThreadAdapterDelegate extends BaseAdapterDelegate<HomeThread, HomeThreadAdapterDelegate.BindingViewHolder> {

    public HomeThreadAdapterDelegate(Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected Class<HomeThread> getTClass() {
        return HomeThread.class;
    }

    @Override
    public void onBindViewHolderData(HomeThread thread, int position, @NonNull BindingViewHolder holder) {
        ItemHomeThreadBinding binding = holder.binding;
        binding.getModel().thread.set(thread);
        binding.executePendingBindings();
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemHomeThreadBinding binding = ItemHomeThreadBinding.inflate(mLayoutInflater, parent, false);
        binding.setModel(new HomeThreadViewModel());
        return new HomeThreadAdapterDelegate.BindingViewHolder(binding);
    }

    static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemHomeThreadBinding binding;

        public BindingViewHolder(ItemHomeThreadBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
