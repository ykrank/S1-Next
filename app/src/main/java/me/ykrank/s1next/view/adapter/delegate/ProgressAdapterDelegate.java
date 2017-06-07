package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import me.ykrank.s1next.R;
import me.ykrank.s1next.databinding.ItemProgressBinding;
import me.ykrank.s1next.view.adapter.item.ProgressItem;

public final class ProgressAdapterDelegate extends BaseAdapterDelegate<ProgressItem, ProgressAdapterDelegate.ProgressViewHolder> {

    public ProgressAdapterDelegate(Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected Class<ProgressItem> getTClass() {
        return ProgressItem.class;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ProgressViewHolder(DataBindingUtil.inflate(mLayoutInflater, R.layout.item_progress, parent, false));
    }

    @Override
    public void onBindViewHolderData(ProgressItem item, int position, @NonNull ProgressViewHolder holder, @NonNull List<Object> payloads) {

    }

    static final class ProgressViewHolder extends RecyclerView.ViewHolder {

        private final ItemProgressBinding binding;

        public ProgressViewHolder(ItemProgressBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
