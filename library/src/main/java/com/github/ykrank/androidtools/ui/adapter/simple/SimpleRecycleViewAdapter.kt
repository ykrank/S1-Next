package com.github.ykrank.androidtools.ui.adapter.simple

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.annotation.LayoutRes
import com.github.ykrank.androidtools.ui.adapter.LibBaseRecyclerViewAdapter
import com.github.ykrank.androidtools.ui.adapter.delegate.item.ProgressItem
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate


/**
 * Simple adapter, just one type item, or [ProgressItem].
 * Layout databinding variable name should be only "model".
 * Created by ykrank on 2017/3/22.
 */

class SimpleRecycleViewAdapter : LibBaseRecyclerViewAdapter {

    constructor(context: Context, @LayoutRes layoutRes: Int, stableId: Boolean = false, bindViewHolderCallback: BindViewHolderCallback? = null,
                createViewHolderCallback: ((ViewDataBinding) -> Unit)? = null) : super(context, stableId) {
        addAdapterDelegate(SimpleAdapterDelegate(context, layoutRes, null, createViewHolderCallback, bindViewHolderCallback))
    }

    constructor(context: Context, adapterDelegate: AdapterDelegate<MutableList<Any>>, stableId: Boolean = false) : super(context, stableId) {
        addAdapterDelegate(adapterDelegate)
    }
}
