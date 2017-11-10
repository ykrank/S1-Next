package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.github.ykrank.androidtools.ui.adapter.delegate.LibBaseAdapterDelegate


abstract class BaseAdapterDelegate<T, in VH : RecyclerView.ViewHolder>(context: Context, entityClass: Class<T>? = null)
    : LibBaseAdapterDelegate<T, VH>(context, entityClass)
