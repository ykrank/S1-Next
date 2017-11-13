package me.ykrank.s1next.view.fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.view.*
import com.github.ykrank.androidtools.ui.LibBaseRecyclerViewFragment
import com.github.ykrank.androidtools.ui.internal.LoadingViewModelBindingDelegate
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiCacheProvider
import me.ykrank.s1next.data.api.ApiFlatTransformer
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.UserValidator
import me.ykrank.s1next.data.api.app.model.AppResult
import me.ykrank.s1next.data.api.model.Result
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.databinding.FragmentBaseBinding
import me.ykrank.s1next.databinding.FragmentBaseCardViewContainerBinding
import me.ykrank.s1next.view.internal.LoadingViewModelBindingDelegateBaseCardViewContainerImpl
import me.ykrank.s1next.view.internal.LoadingViewModelBindingDelegateBaseImpl

/**
 * A base Fragment includes [SwipeRefreshLayout] to refresh when loading data.
 * Also wraps [retrofit2.Retrofit] to loadViewPager data asynchronously.
 *
 *
 * We must call [.destroyRetainedFragment]) if used in [android.support.v4.view.ViewPager]
 * otherwise leads memory leak.
 *
 * @param <D> The data we want to loadViewPager.
</D> */
abstract class BaseRecyclerViewFragment<D> : LibBaseRecyclerViewFragment<D>() {

    internal lateinit var mUserValidator: UserValidator
    internal lateinit var mS1Service: S1Service
    internal lateinit var apiCacheProvider: ApiCacheProvider
    internal lateinit var mDownloadPrefManager: DownloadPreferencesManager
    internal lateinit var mUser: User

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUserValidator = App.appComponent.userValidator
        mS1Service = App.appComponent.s1Service
        apiCacheProvider = App.appComponent.apiCacheProvider
        mDownloadPrefManager = App.preAppComponent.downloadPreferencesManager
        mUser = App.appComponent.user
    }

    @CallSuper
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_base, menu)
    }

    @CallSuper
    override fun onPrepareOptionsMenu(menu: Menu?) {
        // Disables the refresh menu when loading data.
        menu?.findItem(R.id.menu_refresh)?.isEnabled = !isLoading
    }

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_refresh -> {
                startSwipeRefresh()

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Subclass can override this in order to provider different
     * layout for [LoadingViewModelBindingDelegate].
     * run when [.onCreateView]
     */
    override fun getLoadingViewModelBindingDelegateImpl(inflater: LayoutInflater,
                                                        container: ViewGroup?): LoadingViewModelBindingDelegate {
        if (isCardViewContainer) {
            val binding = DataBindingUtil.inflate<FragmentBaseCardViewContainerBinding>(inflater,
                    R.layout.fragment_base_card_view_container, container, false)
            return LoadingViewModelBindingDelegateBaseCardViewContainerImpl(binding)
        } else {
            val binding = DataBindingUtil.inflate<FragmentBaseBinding>(inflater, R.layout.fragment_base,
                    container, false)
            return LoadingViewModelBindingDelegateBaseImpl(binding)
        }
    }

    protected abstract fun getSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<D>

    override fun getLibSourceObservable(loading: Int): Single<D> =
            getSourceObservable(loading)
                    .compose(ApiFlatTransformer.apiErrorTransformer())

    /**
     * Called when a data was emitted from [.getSourceObservable].
     *
     *
     * Actually this method was only called once during loading (if no error occurs)
     * because we only emit data once from [.getSourceObservable].
     */
    @CallSuper
    override fun onNext(data: D) {
        super.onNext(data)
        mUserValidator.validateIntercept(data)
    }

    /**
     * A helper method consumes [Result].
     *
     *
     * Sometimes we cannot get data if we have logged out or
     * have no permission to access this data.
     * This method is only used during [.onNext].
     *
     * @param result The data's result we get.
     */
    protected fun consumeResult(result: Result?) {
        if (isAdded && userVisibleHint) {
            if (result != null) {
                val message = result.message
                if (!TextUtils.isEmpty(message)) {
                    showRetrySnackbar(message)
                }
            } else {
                showRetrySnackbar(R.string.message_server_connect_error)
            }
        }
    }

    protected fun consumeAppResult(result: AppResult?) {
        if (isAdded && userVisibleHint) {
            if (result != null) {
                val message = result.message
                if (!TextUtils.isEmpty(message)) {
                    showRetrySnackbar(message!!)
                }
            } else {
                showRetrySnackbar(R.string.message_server_connect_error)
            }
        }
    }
}
