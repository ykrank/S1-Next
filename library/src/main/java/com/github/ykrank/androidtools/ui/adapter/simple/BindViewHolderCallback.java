package com.github.ykrank.androidtools.ui.adapter.simple;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

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