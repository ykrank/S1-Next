package me.ykrank.s1next.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.view.activity.AppLoginActivity
import me.ykrank.s1next.view.activity.LoginActivity

/**
 * A dialog shows login prompt.
 */
class LoginPromptDialogFragment : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val appLogin = arguments?.getBoolean(APP_LOGIN, false) ?: false
        return AlertDialog.Builder(context!!)
                .setMessage(R.string.dialog_message_login_prompt)
                .setPositiveButton(R.string.action_login) { dialog, which ->
                    if (appLogin) {
                        AppLoginActivity.startLoginActivityForResultMessage(activity)
                    } else {
                        LoginActivity.startLoginActivityForResultMessage(activity)
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
    }

    companion object {

        private val TAG = LoginPromptDialogFragment::class.java.name
        private val APP_LOGIN = "app_login"

        /**
         * Show [LoginPromptDialogFragment] if user hasn't logged in api server.

         * @return `true` if we need to show dialog, `false` otherwise.
         */
        fun showLoginPromptDialogIfNeeded(fm: FragmentManager, user: User): Boolean {
            if (!user.isLogged) {
                val fragment = LoginPromptDialogFragment()
                fragment.show(fm, TAG)

                return true
            }

            return false
        }

        /**
         * Show [LoginPromptDialogFragment] if user hasn't logged in api server or app server.

         * @return `true` if we need to show dialog, `false` otherwise.
         */
        fun showAppLoginPromptDialogIfNeeded(fm: FragmentManager, user: User): Boolean {
            if (!user.isLogged || !user.isAppLogged) {
                val fragment = LoginPromptDialogFragment()
                val bundle = Bundle()
                bundle.putBoolean(APP_LOGIN, user.isLogged)
                fragment.arguments = bundle

                fragment.show(fm, TAG)

                return true
            }

            return false
        }

        fun isShowing(fm: FragmentManager): Boolean {
            val fragment = fm.findFragmentByTag(TAG) as LoginPromptDialogFragment?
            return fragment != null && fragment.dialog?.isShowing ?: false
        }
    }
}
