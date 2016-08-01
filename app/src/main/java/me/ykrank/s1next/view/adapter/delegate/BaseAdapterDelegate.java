package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;

import java.util.List;


public abstract class BaseAdapterDelegate<T, R extends RecyclerView.ViewHolder> extends AbsAdapterDelegate<List<Object>> {
    private OnClickListener<T> onClickListener;

    protected final LayoutInflater mLayoutInflater;

    public BaseAdapterDelegate(Context context, int viewType) {
        super(viewType);

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
    public void onBindViewHolder(@NonNull List<Object> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        final T item = (T) items.get(position);
        if (onClickListener != null) {
            holder.itemView.setOnClickListener(view -> onClickListener.onClick(view, item, position));
        }

        onBindViewHolderData(item, position, (R) holder);
    }

    public final void setOnClickListener(OnClickListener<T> onClickListener) {
        this.onClickListener = onClickListener;
    }

    public abstract void onBindViewHolderData(T t, int position, @NonNull R holder);

    public interface OnClickListener<M> {
        void onClick(View view, M data, int position);
    }
}
