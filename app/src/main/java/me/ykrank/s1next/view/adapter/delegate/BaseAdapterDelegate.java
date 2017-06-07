package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;

import java.util.List;


public abstract class BaseAdapterDelegate<T, VH extends RecyclerView.ViewHolder> extends AdapterDelegate<List<Object>> {
    protected final Class<T> entityClass;
    protected final LayoutInflater mLayoutInflater;

    public BaseAdapterDelegate(Context context) {
        this(context, null);
    }

    public BaseAdapterDelegate(Context context, Class<T> entityClass) {
        mLayoutInflater = LayoutInflater.from(context);
        this.entityClass = entityClass;
    }

    @NonNull
    protected Class<T> getTClass() {
        if (entityClass == null) {
            throw new RuntimeException("Should pass class from constructor , or override getTClass or isForViewType");
        }
        return entityClass;
    }

    @Override
    public boolean isForViewType(@NonNull List<Object> items, int position) {
        return getTClass() == items.get(position).getClass();
    }

    @Override
    @CallSuper
    @SuppressWarnings("unchecked")
    protected void onBindViewHolder(@NonNull List<Object> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        onBindViewHolderData((T) items.get(position), position, (VH) holder, payloads);
    }

    public abstract void onBindViewHolderData(T t, int position, @NonNull VH holder, @NonNull List<Object> payloads);
}
