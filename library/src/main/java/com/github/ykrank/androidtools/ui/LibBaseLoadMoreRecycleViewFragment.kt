package com.github.ykrank.androidtools.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.ui.adapter.LibBaseRecyclerViewAdapter
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import com.github.ykrank.androidtools.util.RxJavaUtil
import io.reactivex.Single

/**
 * Created by ykrank on 2016/11/12 0012.
 */

abstract class LibBaseLoadMoreRecycleViewFragment<D> : LibBaseRecyclerViewFragment<D>() {

    var pageNum = 1
        private set
    protected var mPageCount: Int = 1

    protected abstract val recyclerViewAdapter: LibBaseRecyclerViewAdapter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            pageNum = 1
        } else {
            pageNum = savedInstanceState.getInt(STATE_PAGE_NUM)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                if (!isPullUpToRefresh
                        && pageNum < mPageCount
                        && !isLoading
                        && recyclerViewAdapter.itemCount != 0
                        && !recyclerView.canScrollVertically(1)) {
                    startPullUpLoadMore()
                }
            }
        })
    }


    fun startPullUpLoadMore() {
        recyclerViewAdapter.showFooterProgress()
        setLoading(LoadingViewModel.LOADING_PULL_UP_TO_REFRESH)
        recyclerView.scrollToPosition(recyclerViewAdapter.itemCount)
        loadMore()
    }

    /**
     * Starts to load more data.
     *
     *
     * Subclass should implement [LibBaseRecyclerViewFragment.getSourceObservable]
     * in oder to provider its own data source [Observable].
     */
    private fun loadMore() {
        pageNum++
        // dismiss Snackbar in order to let user see the ProgressBar
        // when we start to loadViewPager new data
        mCoordinatorLayoutAnchorDelegate?.dismissSnackbarIfExist()
        getLibPageSourceObservable(pageNum)
                .map { d -> appendNewData(mBaseRecycleViewModel.data, d) }
                .compose(RxJavaUtil.iOSingleTransformer())
                .doAfterTerminate({ this.finallyDo() })
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe({ this.onLoadMoreNext(it) }, { this.onError(it) })
    }

    /**
     * only when loadMore
     */
    private fun onLoadMoreNext(data: D) {
        onNext(data)
    }

    override fun onError(throwable: Throwable) {
        pageNum--
        recyclerViewAdapter.hideFooterProgress()
        super.onError(throwable)
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(STATE_PAGE_NUM, pageNum)
    }

    protected open fun setTotalPages(pageCount: Int) {
        this.mPageCount = pageCount
    }

    /**
     * append new load data to old data
     *
     * @param oldData data show in recycleView
     * @param newData new load data
     * @return compound data. not same object of oldData, but could newData
     */
    protected abstract fun appendNewData(oldData: D?, newData: D): D

    /**
     * Subclass should implement this in order to provider its
     * data source [Observable].
     *
     *
     * The data source [Observable] often comes from network
     * or database.
     *
     * @return The data source [Observable].
     */
    protected abstract fun getLibPageSourceObservable(pageNum: Int): Single<D>

    override fun getLibSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<D> {
        pageNum = 1
        return getLibPageSourceObservable(1)
    }

    companion object {

        /**
         * The serialization (saved instance state) Bundle key representing
         * current page num.
         */
        private val STATE_PAGE_NUM = "page_num"
    }
}