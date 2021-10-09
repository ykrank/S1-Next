package com.github.ykrank.androidtools.ui.dialog

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.annotation.CallSuper
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.GlobalData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * A dialog shows [ProgressDialog].
 * Also wraps some related methods to request data.
 *
 *
 * This [DialogFragment] is retained in orde
 * to retain request when configuration changes.

 * @param <D> The data we want to request.
</D> */
abstract class LibProgressDialogFragment<D> : LibBaseDialogFragment() {

    protected open val dialogCancelableOnTouchOutside: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // retain this Fragment
        retainInstance = true
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        request()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(getProgressMessage())
        //press back will remove this fragment, so set cancelable no effect
        progressDialog.setCanceledOnTouchOutside(dialogCancelableOnTouchOutside)

        return progressDialog
    }

    @CallSuper
    override fun onDestroyView() {
        // see https://code.google.com/p/android/issues/detail?id=17423
        dialog?.setOnDismissListener(null)

        super.onDestroyView()
    }

    /**
     * @see BaseRecyclerViewFragment.load
     */
    private fun request() {
        getLibSourceObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate { this.finallyDo() }
            .to(AndroidRxDispose.withSingle<D>(this, FragmentEvent.DESTROY))
            .subscribe({ this.onNext(it) }, { this.onError(it) })
    }

    /**
     * @see BaseRecyclerViewFragment.getSourceObservable
     */
    protected abstract fun getLibSourceObservable(): Single<D>

    /**
     * @see BaseRecyclerViewFragment.onNext
     */
    protected abstract fun onNext(data: D)

    /**
     * @see BaseRecyclerViewFragment.onError
     */
    protected open fun onError(throwable: Throwable) {
        val context = context
        if (context != null) {
            GlobalData.provider.errorParser?.let {
                showToastText(it.parse(context, throwable))
            }
        }
    }

    /**
     * @see BaseRecyclerViewFragment.finallyDo
     */
    protected fun finallyDo() {
        dismissAllowingStateLoss()
    }

    protected abstract fun getProgressMessage(): CharSequence?
}