package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.github.ykrank.androidtools.ui.LibBaseLoadMoreRecycleViewFragment
import com.github.ykrank.androidtools.ui.internal.LoadingViewModelBindingDelegate
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
 * Created by ykrank on 2016/11/12 0012.
 */

abstract class BaseLoadMoreRecycleViewFragment<D> : LibBaseLoadMoreRecycleViewFragment<D>() {

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

        setHasOptionsMenu(true)
    }

    @CallSuper
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_base, menu)
    }

    @CallSuper
    override fun onPrepareOptionsMenu(menu: Menu) {
        // Disables the refresh menu when loading data.
        menu?.findItem(R.id.menu_refresh)?.isEnabled = !isLoading
    }

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
                                                        container: ViewGroup?
    ): LoadingViewModelBindingDelegate {
        if (isCardViewContainer) {
            val binding = FragmentBaseCardViewContainerBinding.inflate(inflater, container, false)
            return LoadingViewModelBindingDelegateBaseCardViewContainerImpl(binding)
        } else {
            val binding = FragmentBaseBinding.inflate(inflater, container, false)
            return LoadingViewModelBindingDelegateBaseImpl(binding)
        }
    }

    protected abstract fun getPageSourceObservable(pageNum: Int): Single<D>

    override fun getLibPageSourceObservable(pageNum: Int): Single<D> =
            getPageSourceObservable(pageNum)
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
                if (!message.isNullOrEmpty()) {
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
