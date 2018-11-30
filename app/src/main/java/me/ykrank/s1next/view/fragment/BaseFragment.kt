package me.ykrank.s1next.view.fragment

import android.app.Activity
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast
import com.github.ykrank.androidtools.ui.LibBaseFragment
import me.ykrank.s1next.App
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.UserValidator
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.view.activity.BaseActivity
import javax.inject.Inject

abstract class BaseFragment : LibBaseFragment() {

    @Inject
    internal lateinit var mUserValidator: UserValidator
    @Inject
    internal lateinit var mUser: User

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.appComponent.inject(this)
    }

    fun showRetrySnackbar(throwable: Throwable, onClickListener: View.OnClickListener) {
        val context = context ?: return
        showRetrySnackbar(ErrorUtil.parse(context, throwable), onClickListener)
    }

    protected fun showShortSnackbar(throwable: Throwable) {
        val context = context ?: return
        showShortSnackbar(ErrorUtil.parse(context, throwable))
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
