package me.ykrank.s1next.view.adapter.simple;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

public class SimpleRecycleViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    final T binding;

    public SimpleRecycleViewHolder(T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public T getBinding() {
        return binding;
    }
}