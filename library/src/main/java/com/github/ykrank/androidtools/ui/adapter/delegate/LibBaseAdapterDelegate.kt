package com.github.ykrank.androidtools.ui.adapter.delegate

import android.content.Context
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate


abstract class LibBaseAdapterDelegate<T, in VH : androidx.recyclerview.widget.RecyclerView.ViewHolder>
(context: Context, protected val entityClass: Class<T>? = null) : AdapterDelegate<MutableList<Any>>() {
    protected val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    public override fun isForViewType(items: MutableList<Any>, position: Int): Boolean {
        checkNotNull(entityClass, {"Should pass class from constructor , or override getTClass or isForViewType"})
        return entityClass == items[position].javaClass
    }

    @CallSuper
    override fun onBindViewHolder(items: MutableList<Any>, position: Int, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, payloads: List<Any>) {
        onBindViewHolderData(items[position] as T, position, holder as VH, payloads)
    }

    abstract fun onBindViewHolderData(t: T, position: Int, holder: VH, payloads: List<Any>)
}
