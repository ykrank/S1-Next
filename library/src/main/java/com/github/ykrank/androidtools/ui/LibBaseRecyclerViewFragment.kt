package com.github.ykrank.androidtools.ui

import android.os.Bundle
import android.os.SystemClock
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.ykrank.androidtools.GlobalData
import com.github.ykrank.androidtools.ui.internal.DataRetainedFragment
import com.github.ykrank.androidtools.ui.internal.LoadingViewModelBindingDelegate
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable

/**
 * A base Fragment includes [SwipeRefreshLayout] to refresh when recycleview_loading data.
 * Also wraps [retrofit2.Retrofit] to loadViewPager data asynchronously.
 *
 *
 * We must call [.destroyRetainedFragment]) if used in [android.support.v4.view.ViewPager]
 * otherwise leads memory leak.
 *
 * @param <D> The data we want to loadViewPager.
</D> */
abstract class LibBaseRecyclerViewFragment<D> : LibBaseFragment() {

    private lateinit var mLoadingViewModelBindingDelegate: LoadingViewModelBindingDelegate
    private var mLoadingViewModel: LoadingViewModel = LoadingViewModel()

    /**
     * We use retained Fragment to retain data when configuration changes.
     */
    protected lateinit var retainedFragment: DataRetainedFragment<D>
        private set

    private var mDisposable: Disposable? = null
    private var lastPullRefreshTime: Long = 0
    private var init = false

    /**
     * the id of DataRetainedFragment's data
     *
     * @return id
     */
    open val dataId: String? = null

    /**
     * whether use ?attr/cardViewContainerBackground to this fragment. if override [.getLoadingViewModelBindingDelegateImpl]
     * perhaps ignore this
     */
    protected open val isCardViewContainer: Boolean = false

    /**
     * Whether we are recycleview_loading data now.
     */
    protected val isLoading: Boolean
        get() = mLoadingViewModel.loading != LoadingViewModel.LOADING_FINISH

    /**
     * Whether we are recycleview_loading data manual.
     */
    protected val isForceLoading: Boolean
        get() = mLoadingViewModel.loading == LoadingViewModel.LOADING_PULL_UP_TO_REFRESH || mLoadingViewModel.loading == LoadingViewModel.LOADING_SWIPE_REFRESH

    /**
     * Whether we are pulling up to refresh.
     */
    protected val isPullUpToRefresh: Boolean
        get() = mLoadingViewModel.loading == LoadingViewModel.LOADING_PULL_UP_TO_REFRESH

    /**
     * whether we not pull up refresh too frequently
     */
    protected val isPullUpToRefreshValid: Boolean
        get() {
            val lastTime = SystemClock.elapsedRealtime() - lastPullRefreshTime
            return lastTime > PULL_REFRESH_COLD_TIME
        }

    protected val recyclerView: androidx.recyclerview.widget.RecyclerView
        get() = mLoadingViewModelBindingDelegate.recyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLoadingViewModelBindingDelegate = getLoadingViewModelBindingDelegateImpl(inflater,
                container)
        return mLoadingViewModelBindingDelegate.rootView
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLoadingViewModelBindingDelegate.swipeRefreshLayout.setOnRefreshListener(
                { this.startSwipeRefresh() })
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // because we can't retain Fragments that are nested in other Fragments
        // so we need to confirm this Fragment has unique tag in order to compose
        // a new unique tag for its retained Fragment.
        // Without this, we couldn't get its retained Fragment back.
        assert(tag != null) { "Must add a tag to $this." }
        val dataRetainedFragmentTag = DataRetainedFragment.TAG + "_" + tag
        val fragmentManager = fragmentManager ?: return
        val fragment = fragmentManager.findFragmentByTag(dataRetainedFragmentTag)
        if (fragment == null) {
            retainedFragment = DataRetainedFragment()
            fragmentManager.beginTransaction().add(retainedFragment, dataRetainedFragmentTag)
                    .commitAllowingStateLoss()

            // start to loadViewPager data because we start this Fragment the first time
            mLoadingViewModel.loading = LoadingViewModel.LOADING_FIRST_TIME
        } else {
            retainedFragment = fragment as DataRetainedFragment<D>

            // get data back from retained Fragment when configuration changes
            if (retainedFragment.data != null) {
                if (dataId == retainedFragment.id) {
                    val loading = mLoadingViewModel.loading
                    onNext(retainedFragment.data)
                    mLoadingViewModel.loading = loading
                } else {
                    //data id changed, so it's invalid
                    mLoadingViewModel.loading = LoadingViewModel.LOADING_FIRST_TIME
                }
            } else {
                if (!retainedFragment.stale) {
                    // start to loadViewPager data because the retained Fragment was killed by system
                    // and we have no data to loadViewPager
                    mLoadingViewModel.loading = LoadingViewModel.LOADING_FIRST_TIME
                }
            }
        }

        mLoadingViewModelBindingDelegate.setLoadingViewModel(mLoadingViewModel)

        if (!isLazyLoad() || mUserVisibleHint) {
            init = true
            load(mLoadingViewModel.loading)
        }
    }

    override fun onDestroy() {
        //remove OnRefreshListener
        mLoadingViewModelBindingDelegate.swipeRefreshLayout.setOnRefreshListener(null)
        RxJavaUtil.disposeIfNotNull(mDisposable)

        super.onDestroy()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(STATE_LOADING_VIEW_MODEL, mLoadingViewModel.loading)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isVisible) {
            //Create but only init or Init but should refresh when reVisible
            if (!init || init && refreshWhenUserVisibleHint()) {
                init = true
                startSwipeRefresh()
            }
        }
    }

    /**
     * Subclass can override this in order to provider different
     * layout for [LoadingViewModelBindingDelegate].
     * run when [.onCreateView]
     */
    open abstract fun getLoadingViewModelBindingDelegateImpl(inflater: LayoutInflater,
                                                             container: ViewGroup?): LoadingViewModelBindingDelegate

    /**
     * Whether load when visible in viewpager.
     * Should return persist value
     */
    protected open fun isLazyLoad(): Boolean {
        return false
    }

    /**
     * Whether refresh when UserVisibleHint change from invisible to visible in viewpager
     * Should return persist value
     */
    protected open fun refreshWhenUserVisibleHint(): Boolean {
        return false
    }

    protected fun setLoading(@LoadingViewModel.LoadingDef loading: Int) {
        mLoadingViewModel.loading = loading
    }

    /**
     * Show refresh progress and start to loadViewPager new data.
     */
    @CallSuper
    fun startSwipeRefresh() {
        mLoadingViewModel.loading = LoadingViewModel.LOADING_SWIPE_REFRESH
        load(LoadingViewModel.LOADING_SWIPE_REFRESH)
    }

    /**
     * Disables [SwipeRefreshLayout] and start to loadViewPager new data.
     *
     *
     * Subclass should override this method and add [android.widget.ProgressBar]
     * to `getRecyclerView()` in order to let [.showRetrySnackbar]
     * work.
     */
    @CallSuper
    protected open fun startPullToRefresh() {
        if (isPullUpToRefreshValid) {
            lastPullRefreshTime = SystemClock.elapsedRealtime()
            mLoadingViewModel.loading = LoadingViewModel.LOADING_PULL_UP_TO_REFRESH
            load(LoadingViewModel.LOADING_PULL_UP_TO_REFRESH)
        }
    }

    /**
     * Starts to loadViewPager new data.
     *
     *
     * Subclass should implement [.getSourceObservable]
     * in oder to provider its own data source [Observable].
     */
    private fun load(@LoadingViewModel.LoadingDef loading: Int) {
        if (loading == LoadingViewModel.LOADING_FINISH) {
            return
        }
        onLoad(loading)
        // dismiss Snackbar in order to let user see the ProgressBar
        // when we start to loadViewPager new data
        mCoordinatorLayoutAnchorDelegate?.dismissSnackbarIfExist()
        RxJavaUtil.disposeIfNotNull(mDisposable)
        mDisposable = getLibSourceObservable(loading)
                .compose(RxJavaUtil.iOSingleTransformer())
                .doAfterTerminate { this.finallyDo() }
                .subscribe({ this.onNext(it) }, { this.onError(it) })
    }

    /**
     * DO something on load data
     */
    @MainThread
    protected open fun onLoad(@LoadingViewModel.LoadingDef loading: Int) {

    }

    /**
     * Subclass should implement this in order to provider its
     * data source [Observable].
     *
     *
     * The data source [Observable] often comes from network
     * or database.
     *
     * @return The data source [Observable].
     * @param loading
     */
    protected abstract fun getLibSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<D>

    /**
     * Called when a data was emitted from [.getSourceObservable].
     *
     *
     * Actually this method was only called once during recycleview_loading (if no error occurs)
     * because we only emit data once from [.getSourceObservable].
     */
    @CallSuper
    protected open fun onNext(data: D) {
        retainedFragment.data = data
    }

    /**
     * Called when an error occurs during data recycleview_loading.
     *
     *
     * This stops the [.getSourceObservable] and it will not make
     * further calls to [.onNext].
     */
    @CallSuper
    protected open fun onError(throwable: Throwable) {
        L.print(throwable)
        GlobalData.provider.errorParser?.let {
            val context = context
            if (context != null && isAdded && userVisibleHint) {
                showRetrySnackbar(it.parse(context, throwable))
            }
        }
    }

    /**
     * Called if it will not make further calls to [.onNext]
     * or [.onError] occurred during data recycleview_loading.
     */
    @CallSuper
    protected open fun finallyDo() {
        mLoadingViewModel.loading = LoadingViewModel.LOADING_FINISH
        retainedFragment.stale = true
        retainedFragment.id = dataId
    }

    fun showRetrySnackbar(text: CharSequence) {
        showRetrySnackbar(text, if (isPullUpToRefresh)
            View.OnClickListener { startPullToRefresh() }
        else
            View.OnClickListener { startSwipeRefresh() })
    }

    protected fun showRetrySnackbar(@StringRes textResId: Int) {
        showRetrySnackbar(getString(textResId))
    }

    /**
     * We must call this if used in [android.support.v4.view.ViewPager]
     * otherwise leads memory leak.
     */
    override fun destroyRetainedFragment() {
        fragmentManager?.let { it.beginTransaction().remove(retainedFragment).commitNowAllowingStateLoss() }
    }

    companion object {

        /**
         * The serialization (saved instance state) Bundle key representing
         * current recycleview_loading state.
         */
        private val STATE_LOADING_VIEW_MODEL = "loading_view_model"
        private val PULL_REFRESH_COLD_TIME: Long = 1000
    }
}
