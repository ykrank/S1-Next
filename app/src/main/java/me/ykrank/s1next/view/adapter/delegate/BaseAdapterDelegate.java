package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;

import java.util.List;


public abstract class BaseAdapterDelegate<T, VH extends RecyclerView.ViewHolder> extends AdapterDelegate<List<Object>> {
    protected final LayoutInflater mLayoutInflater;

    public BaseAdapterDelegate(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    protected abstract Class<T> getTClass();

    @Override
    @CallSuper
    public boolean isForViewType(@NonNull List<Object> items, int position) {
        return getTClass().isInstance(items.get(position));
    }

    @Override
    @CallSuper
    @SuppressWarnings("unchecked")
    protected void onBindViewHolder(@NonNull List<Object> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        onBindViewHolderData((T) items.get(position), position, (VH) holder);
    }

    public abstract void onBindViewHolderData(T t, int position, @NonNull VH holder);
}
