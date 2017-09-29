package me.ykrank.s1next.view.adapter.simple;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * when bind ViewHolder with view
 */
public interface BindViewHolderCallback {
    /**
     * when {@link SimpleAdapterDelegate#onBindViewHolder(Object, int, RecyclerView.ViewHolder, List)}
     *
     * @param binding store view and data model
     */
    void onBindViewHolder(int position, ViewDataBinding binding);
}