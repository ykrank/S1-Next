package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.github.ykrank.androidtools.ui.adapter.delegate.LibBaseAdapterDelegate


abstract class BaseAdapterDelegate<T, in VH : androidx.recyclerview.widget.RecyclerView.ViewHolder>(context: Context, entityClass: Class<T>? = null)
    : LibBaseAdapterDelegate<T, VH>(context, entityClass)
