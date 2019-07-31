package me.ykrank.s1next.view.dialog

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import androidx.annotation.CallSuper
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.DialogFragment
import android.widget.Toast
import com.github.ykrank.androidtools.ui.dialog.LibProgressDialogFragment
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiFlatTransformer
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.UserValidator
import me.ykrank.s1next.view.activity.BaseActivity
import me.ykrank.s1next.view.fragment.BaseRecyclerViewFragment

/**
 * A dialog shows [ProgressDialog].
 * Also wraps some related methods to request data.
 *
 *
 * This [DialogFragment] is retained in orde
 * to retain request when configuration changes.

 * @param <D> The data we want to request.
</D> */
abstract class ProgressDialogFragment<D> : LibProgressDialogFragment<D>() {
    internal lateinit var mS1Service: S1Service
    internal lateinit var mUserValidator: UserValidator
    internal lateinit var mUser: User

    private var dialogNotCancelableOnTouchOutside: Boolean = false

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = App.appComponent
        mS1Service = appComponent.s1Service
        mUser = appComponent.user
        mUserValidator = appComponent.userValidator
    }

    /**
     * @see BaseRecyclerViewFragment.getSourceObservable
     */
    protected abstract fun getSourceObservable(): Single<D>

    override fun getLibSourceObservable(): Single<D> {
        return getSourceObservable()
                .compose(ApiFlatTransformer.apiErrorTransformer<D>())
    }

    /**
     * @see ApiFlatTransformer.flatMappedWithAuthenticityToken
     */
    protected fun flatMappedWithAuthenticityToken(func: (String) -> Single<D>): Single<D> {
        return ApiFlatTransformer.flatMappedWithAuthenticityToken(mS1Service, mUserValidator, mUser, func)
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
        val activity = activity ?: return
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
}
