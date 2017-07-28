package me.ykrank.s1next.view.dialog

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.widget.Toast
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.ykrank.s1next.App
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiFlatTransformer
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.UserValidator
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.view.activity.BaseActivity
import me.ykrank.s1next.view.fragment.BaseRecyclerViewFragment
import me.ykrank.s1next.view.internal.CoordinatorLayoutAnchorDelegate

/**
 * A dialog shows [ProgressDialog].
 * Also wraps some related methods to request data.
 *
 *
 * This [DialogFragment] is retained in order
 * to retain request when configuration changes.

 * @param <D> The data we want to request.
</D> */
abstract class ProgressDialogFragment<D> : BaseDialogFragment() {

    protected lateinit var mS1Service: S1Service

    protected lateinit var mUserValidator: UserValidator

    private lateinit var mUser: User

    private var dialogNotCancelableOnTouchOutside: Boolean = false

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = App.getAppComponent()
        mS1Service = appComponent.s1Service
        mUser = appComponent.user
        mUserValidator = appComponent.userValidator

        if (arguments != null) {
            dialogNotCancelableOnTouchOutside = arguments.getBoolean(ARG_DIALOG_NOT_CANCELABLE_ON_TOUCH_OUTSIDE, false)
        }

        // retain this Fragment
        retainInstance = true

        request()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(getProgressMessage())
        //press back will remove this fragment, so set cancelable no effect
        progressDialog.setCanceledOnTouchOutside(!dialogNotCancelableOnTouchOutside)

        return progressDialog
    }

    @CallSuper
    override fun onDestroyView() {
        // see https://code.google.com/p/android/issues/detail?id=17423
        val dialog = dialog
        if (dialog != null) {
            getDialog().setOnDismissListener(null)
        }

        super.onDestroyView()
    }

    /**
     * @see BaseRecyclerViewFragment.load
     */
    private fun request() {
        getSourceObservable()
                .compose(ApiFlatTransformer.apiErrorTransformer<D>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate { this.finallyDo() }
                .to(AndroidRxDispose.withObservable<D>(this, FragmentEvent.DESTROY))
                .subscribe({ this.onNext(it) }, { this.onError(it) })
    }

    /**
     * @see BaseRecyclerViewFragment.getSourceObservable
     */
    protected abstract fun getSourceObservable(): Observable<D>

    /**
     * @see ApiFlatTransformer.flatMappedWithAuthenticityToken
     */
    protected fun flatMappedWithAuthenticityToken(func: (String) -> Observable<D>): Observable<D> {
        return ApiFlatTransformer.flatMappedWithAuthenticityToken(mS1Service, mUserValidator, mUser, func)
    }

    /**
     * @see BaseRecyclerViewFragment.onNext
     */
    protected abstract fun onNext(data: D)

    /**
     * @see BaseRecyclerViewFragment.onError
     */
    internal open fun onError(throwable: Throwable) {
        showShortText(ErrorUtil.parse(context, throwable))
    }

    /**
     * @see BaseRecyclerViewFragment.finallyDo
     */
    protected fun finallyDo() {
        dismissAllowingStateLoss()
    }

    /**
     * @see me.ykrank.s1next.view.activity.BaseActivity.showShortText
     */
    protected fun showShortText(text: CharSequence?) {
        (activity as CoordinatorLayoutAnchorDelegate).showShortText(text)
    }

    /**
     * If current [android.app.Activity] is visible, sets result message to [Activity]
     * in order to show a short [Snackbar] for message during [.onActivityResult],
     * otherwise show a short [android.widget.Toast].

     * @param text The text to show.
     * *
     * @see BaseActivity.onActivityResult
     */
    protected fun showShortTextAndFinishCurrentActivity(text: CharSequence?) {
        val activity = activity
        val app = activity.applicationContext as App
        // Because Activity#onActivityResult(int, int, Intent) is always invoked when current app
        // is running in the foreground (so we are unable to set result message to Activity to
        // let BaseActivity to show a Toast if our app is running in the background).
        // So we need to handle it by ourselves.
        if (app.isAppVisible) {
            BaseActivity.setResultMessage(activity, text)
        } else {
            Toast.makeText(app, text, Toast.LENGTH_SHORT).show()
        }

        activity.finish()
    }

    protected abstract fun getProgressMessage(): CharSequence?

    companion object {

        internal val ARG_DIALOG_NOT_CANCELABLE_ON_TOUCH_OUTSIDE = "dialog_not_cancelable_on_touch_outside"
    }
}
