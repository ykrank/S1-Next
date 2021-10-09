package com.github.ykrank.androidtools.ui.adapter.simple

import android.content.Context
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

/**
 * Created by ykrank on 2016/8/1 0001.
 */
abstract class SimpleViewHolderAdapter<D, VH : SimpleViewHolderAdapter.BaseViewHolder>(context: Context, @param:LayoutRes @field:LayoutRes
protected var mResource: Int, objects: List<D>) : ArrayAdapter<D>(context, mResource, objects) {

    protected var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView_: View?, parent: ViewGroup): View {
        var convertView = convertView_
        val viewHolder: VH
        if (convertView == null) {
            viewHolder = onCreateViewHolder(parent)
            convertView = viewHolder.root
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as VH
        }
        onBindViewHolder(viewHolder, getItem(position), position)
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.hashCode()?.toLong() ?: position.toLong()
    }

    abstract fun onCreateViewHolder(parent: ViewGroup): VH

    abstract fun onBindViewHolder(viewHolder: VH, data: D?, position: Int)

    abstract class BaseViewHolder(val root: View)
}
