package com.github.ykrank.androidtools.ui.adapter

import androidx.recyclerview.widget.GridLayoutManager
import com.github.ykrank.androidtools.ui.adapter.delegate.item.FooterProgressItem
import com.github.ykrank.androidtools.ui.adapter.delegate.item.ProgressItem
import java.lang.ref.WeakReference

/**
 * Use to lookup progress item position to match_parent
 * @param adapter weak reference in this class
 */
class BaseGridSpanSizeLookup(val spanCount: Int, adapter: LibBaseRecyclerViewAdapter) : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
    private val adapterRef = WeakReference(adapter)

    override fun getSpanSize(position: Int): Int {
        if (isProgressItem(position) || isFooterProgressItem(position)) {
            return spanCount
        }
        return 1
    }

    override fun isSpanIndexCacheEnabled(): Boolean {
        return true
    }

    private fun isProgressItem(position: Int): Boolean {
        return adapterRef.get()?.getItem(position) is ProgressItem
    }

    private fun isFooterProgressItem(position: Int): Boolean {
        return adapterRef.get()?.getItem(position) is FooterProgressItem
    }
}