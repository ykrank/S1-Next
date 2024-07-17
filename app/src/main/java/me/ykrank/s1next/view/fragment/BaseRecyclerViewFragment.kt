package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.ykrank.androidtools.data.Resource
import com.github.ykrank.androidtools.ui.LibBaseRecyclerViewFragment
import com.github.ykrank.androidtools.ui.internal.LoadingViewModelBindingDelegate
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiCacheProvider
import me.ykrank.s1next.data.api.ApiFlatTransformer
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.UserValidator
import me.ykrank.s1next.data.api.app.model.AppResult
import me.ykrank.s1next.data.api.model.Result
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
    internal lateinit var mUser: User

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUserValidator = App.appComponent.userValidator
        mS1Service = App.appComponent.s1Service
        apiCacheProvider = App.appComponent.apiCacheProvider
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
                                                        container: ViewGroup?): LoadingViewModelBindingDelegate<D> {
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

    protected open fun getSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<D>? {
        return null
    }

    override fun getLibSourceObservable(loading: Int): Single<D>? =
            getSourceObservable(loading)
                ?.compose(ApiFlatTransformer.apiErrorTransformer())

    protected open suspend fun getSource(@LoadingViewModel.LoadingDef loading: Int): Flow<Resource<D>>? {
        return null
    }

    override suspend fun getLibSource(loading: Int): Flow<Resource<D>>? {
        return getSource(loading)?.map {
            val error = it.data?.let {
                ApiFlatTransformer.getApiResultError(it)
            }
            if (error != null) {
                return@map Resource.Error(it.source, error, it.data)
            }
            it
        }
    }

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
    protected open fun consumeResult(result: Result?) {
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
