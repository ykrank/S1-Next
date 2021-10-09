package com.github.ykrank.androidtools.ui.adapter.simple

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.ykrank.androidtools.GlobalData
import com.github.ykrank.androidtools.ui.adapter.delegate.item.FooterProgressItem
import com.github.ykrank.androidtools.ui.adapter.delegate.item.ProgressItem
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate

open class SimpleAdapterDelegate constructor(context: Context, @param:LayoutRes private val layoutRes: Int,
                                             private val modelClass: Class<*>? = null,
                                             private val createViewHolderCallback: ((ViewDataBinding) -> Unit)? = null,
                                             private val bindViewHolderCallback: BindViewHolderCallback? = null) : AdapterDelegate<MutableList<Any>>() {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun isForViewType(items: MutableList<Any>, position: Int): Boolean {
        val item = items[position]
        if (modelClass == null) {
            return !ProgressItem::class.java.isInstance(item) && !FooterProgressItem::class.java.isInstance(item)
        } else {
            return modelClass.isInstance(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(mLayoutInflater, layoutRes, parent, false)
        createViewHolderCallback?.invoke(binding)
        return SimpleRecycleViewHolder(binding)
    }

    override fun onBindViewHolder(items: MutableList<Any>, position: Int, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, payloads: List<Any>) {
        val binding = (holder as SimpleRecycleViewHolder<*>).binding
        binding.setVariable(GlobalData.provider.itemModelBRid, items[position])
        bindViewHolderCallback?.onBindViewHolder(position, binding)
    }
}