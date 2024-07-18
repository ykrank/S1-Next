package com.github.ykrank.androidtools.ui.internal

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel

/**
 * This class represents a delegate which you can bind
 * [LoadingViewModel] to different [androidx.databinding.ViewDataBinding]s
 * in implementation.
 */
interface LoadingViewModelBindingDelegate {
    val rootView: View
    val swipeRefreshLayout: SwipeRefreshLayout

    /**
     * This [RecyclerView] should always set a
     * [LibBaseRecyclerViewAdapter]
     * implementation, otherwise we can not use
     * [LibBaseRecyclerViewAdapter.setHasProgress].
     */
    val recyclerView: RecyclerView

    val hintView: TextView
    fun setLoadingViewModel(loadingViewModel: LoadingViewModel)
}
