package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Pm;
import me.ykrank.s1next.databinding.ItemPmBinding;
import me.ykrank.s1next.viewmodel.PmViewModel;

public final class PmAdapterDelegate extends BaseAdapterDelegate<Pm, PmAdapterDelegate.BindingViewHolder> {

    public PmAdapterDelegate(Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected Class<Pm> getTClass() {
        return Pm.class;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemPmBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_pm, parent, false);
        binding.setPmViewModel(new PmViewModel());
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolderData(Pm pm, int position, @NonNull BindingViewHolder holder) {
        ItemPmBinding binding = holder.binding;
        binding.getPmViewModel().pm.set(pm);
        binding.executePendingBindings();
    }

    /**
     * make textview selectable
     * @param holder
     */
    @Override
    protected void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ItemPmBinding binding = ((BindingViewHolder)holder).binding;
        binding.authorName.setEnabled(false);
        binding.authorName.setEnabled(true);
        binding.tvMessage.setEnabled(false);
        binding.tvMessage.setEnabled(true);
    }

    static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemPmBinding binding;

        public BindingViewHolder(ItemPmBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
