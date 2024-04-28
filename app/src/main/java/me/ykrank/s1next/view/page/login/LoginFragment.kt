package me.ykrank.s1next.view.page.login


/**
 * A Fragment offers login via username and password.
 */
class LoginFragment : BaseLoginFragment() {
    override fun showLoginDialog(
        username: String,
        password: String,
        questionId: Int?,
        answer: String?
    ) {
        LoginDialogFragment.newInstance(username, password, questionId, answer).show(
            fragmentManager!!,
            AppLoginDialogFragment.TAG
        )
    }

    companion object {
        @JvmField
        val TAG = LoginFragment::class.java.getName()
    }
}
