package me.ykrank.s1next.view.adapter;

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

/**
 * Simple adapter, just one type item, or {@link me.ykrank.s1next.view.adapter.item.ProgressItem}.
 * Layout databinding variable name should be only "model".
 * Created by ykrank on 2017/3/22.
 */

public class SimpleRecycleViewAdapter extends BaseRecyclerViewAdapter {
    
    public SimpleRecycleViewAdapter(@NonNull Context context, @LayoutRes int layoutRes) {
        this(context, layoutRes, null);
    }

    public SimpleRecycleViewAdapter(@NonNull Context context, @LayoutRes int layoutRes, BindViewHolderCallback bindViewHolderCallback) {
        super(context);
        addAdapterDelegate(new SimpleAdapterDelegate(context, layoutRes, bindViewHolderCallback));
    }
    
    public static class SimpleAdapterDelegate extends AdapterDelegate<List<Object>> {
        final LayoutInflater mLayoutInflater;
        final int layoutRes;
        @Nullable
        final BindViewHolderCallback bindViewHolderCallback;
        
        SimpleAdapterDelegate(Context context, @LayoutRes int layoutRes, @Nullable BindViewHolderCallback bindViewHolderCallback){
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
            return new SimpleViewHolder(binding);
        }

        @Override
        protected void onBindViewHolder(@NonNull List<Object> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
            ViewDataBinding binding = ((SimpleViewHolder)holder).binding;
            binding.setVariable(BR.model, items.get(position));
            if (bindViewHolderCallback != null){
                bindViewHolderCallback.onBindViewHolder(binding);
            }
        }
    }
    
    public static class SimpleViewHolder extends RecyclerView.ViewHolder{
         final ViewDataBinding binding;

        public SimpleViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    /**
     * when bind ViewHolder with view
     */
    public interface BindViewHolderCallback{
        /**
         * when {@link BaseRecyclerViewAdapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
         * @param binding store view and data model
         */
        void onBindViewHolder(ViewDataBinding binding);
    }
}
