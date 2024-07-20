package me.ykrank.s1next.view.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.github.ykrank.androidtools.util.WebViewUtils.clearWebViewCookies
import com.github.ykrank.androidtools.widget.RxBus
import me.ykrank.s1next.App.Companion.appComponent
import me.ykrank.s1next.App.Companion.get
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.view.event.LoginEvent
import me.ykrank.s1next.viewmodel.UserViewModel
import java.net.CookieManager
import javax.inject.Inject

/**
 * A dialog shows logout prompt.
 * Logs out if user clicks the logout button.
 */
class LogoutDialogFragment : BaseDialogFragment() {

    @Inject
    lateinit var mCookieManager: CookieManager


    @Inject
    lateinit var mUser: UserViewModel

    @Inject
    lateinit var mRxBus: RxBus

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        appComponent.inject(this)
        return AlertDialog.Builder(requireContext())
            .setMessage(R.string.dialog_message_log_out)
            .setPositiveButton(R.string.dialog_button_text_log_out) { dialog: DialogInterface?, which: Int -> logout() }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    /**
     * Clears user's cookies and current user's info.
     */
    private fun logout() {
        mCookieManager.cookieStore.removeAll()
        clearWebViewCookies(get())
        mUser.user.appSecureToken = null
        mUser.user.isLogged = false
        mRxBus.post(LoginEvent())
    }

    companion object {
        private val TAG: String = LogoutDialogFragment::class.java.name

        /**
         * Show [LogoutDialogFragment] if user has logged in.
         *
         * @return `true` if we need to show dialog, `false` otherwise.
         */
        fun showLogoutDialogIfNeeded(fragmentActivity: FragmentActivity, user: User): Boolean {
            if (user.isLogged) {
                LogoutDialogFragment().show(
                    fragmentActivity.supportFragmentManager,
                    TAG
                )

                return true
            }

            return false
        }
    }
}
