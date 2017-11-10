package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import com.github.ykrank.androidtools.util.RxJavaUtil
import io.reactivex.Observable
import io.reactivex.Single
import me.ykrank.s1next.data.api.ApiFlatTransformer
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter

/**
 * Created by ykrank on 2016/11/12 0012.
 */

abstract class BaseLoadMoreRecycleViewFragment<D> : BaseRecyclerViewFragment<D>() {

    var pageNum = 1
        private set
    private var mPageCount: Int = 0

    internal abstract val recyclerViewAdapter: BaseRecyclerViewAdapter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            pageNum = 1
        } else {
            pageNum = savedInstanceState.getInt(STATE_PAGE_NUM)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
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
     * Subclass should implement [BaseRecyclerViewFragment.getSourceObservable]
     * in oder to provider its own data source [Observable].
     */
    private fun loadMore() {
        pageNum++
        // dismiss Snackbar in order to let user see the ProgressBar
        // when we start to loadViewPager new data
        mCoordinatorLayoutAnchorDelegate?.dismissSnackbarIfExist()
        getPageSourceObservable(pageNum)
                .map { d -> appendNewData(retainedFragment.data, d) }
                .compose(ApiFlatTransformer.apiErrorTransformer())
                .compose(RxJavaUtil.iOSingleTransformer())
                .doOnSuccess({ mUserValidator.validateIntercept(it) })
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
        super.onError(throwable)
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putInt(STATE_PAGE_NUM, pageNum)
    }

    internal fun setTotalPages(pageCount: Int) {
        this.mPageCount = pageCount
    }

    /**
     * append new load data to old data
     *
     * @param oldData data show in recycleView
     * @param newData new load data
     * @return compound data. not same object of oldData, but could newData
     */
    internal abstract fun appendNewData(oldData: D?, newData: D): D

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
    internal abstract fun getPageSourceObservable(pageNum: Int): Single<D>

    override fun getSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<D> {
        pageNum = 1
        return getPageSourceObservable(1)
    }

    companion object {

        /**
         * The serialization (saved instance state) Bundle key representing
         * current page num.
         */
        private val STATE_PAGE_NUM = "page_num"
    }
}
