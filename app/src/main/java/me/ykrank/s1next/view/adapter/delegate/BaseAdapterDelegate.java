package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;

import java.util.List;


public abstract class BaseAdapterDelegate<T, VH extends RecyclerView.ViewHolder> extends AdapterDelegate<List<Object>> {
    private OnClickListener<T> onClickListener;

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
        final T item = (T) items.get(position);
        if (onClickListener != null) {
            holder.itemView.setOnClickListener(view -> onClickListener.onClick(view, item, position));
        }

        onBindViewHolderData(item, position, (VH) holder);
    }

    public final void setOnClickListener(OnClickListener<T> onClickListener) {
        this.onClickListener = onClickListener;
    }

    public abstract void onBindViewHolderData(T t, int position, @NonNull VH holder);

    public interface OnClickListener<M> {
        void onClick(View view, M data, int position);
    }
}
