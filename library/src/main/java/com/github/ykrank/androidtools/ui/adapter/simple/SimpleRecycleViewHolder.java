package com.github.ykrank.androidtools.ui.adapter.simple;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

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