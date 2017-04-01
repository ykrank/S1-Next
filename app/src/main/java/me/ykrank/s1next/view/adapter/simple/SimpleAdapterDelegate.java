package me.ykrank.s1next.view.adapter.simple;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;

import java.util.List;

import me.ykrank.s1next.BR;
import me.ykrank.s1next.view.adapter.item.ProgressItem;

public class SimpleAdapterDelegate extends AdapterDelegate<List<Object>> {
    private final LayoutInflater mLayoutInflater;
    private final int layoutRes;
    @Nullable
    private final BindViewHolderCallback bindViewHolderCallback;

    SimpleAdapterDelegate(Context context, @LayoutRes int layoutRes, @Nullable BindViewHolderCallback bindViewHolderCallback) {
        mLayoutInflater = LayoutInflater.from(context);
        this.layoutRes = layoutRes;
        this.bindViewHolderCallback = bindViewHolderCallback;
    }

    @Override
    protected boolean isForViewType(@NonNull List<Object> items, int position) {
        return !ProgressItem.class.isInstance(items.get(position));
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ViewDataBinding binding = DataBindingUtil.inflate(mLayoutInflater, layoutRes, parent, false);
        return new SimpleRecycleViewHolder<>(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Object> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        ViewDataBinding binding = ((SimpleRecycleViewHolder) holder).binding;
        binding.setVariable(BR.model, items.get(position));
        if (bindViewHolderCallback != null) {
            bindViewHolderCallback.onBindViewHolder(binding);
        }
        binding.executePendingBindings();
    }
}