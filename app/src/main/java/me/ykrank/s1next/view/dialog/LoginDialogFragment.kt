package me.ykrank.s1next.view.dialog

import android.os.Bundle
import com.github.ykrank.androidtools.widget.RxBus
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper
import me.ykrank.s1next.view.event.LoginEvent
import javax.inject.Inject

/**
 * A [ProgressDialogFragment] posts a request to login to server.
 */
class LoginDialogFragment : ProgressDialogFragment<AccountResultWrapper>() {

    @Inject
    internal lateinit var mRxBus: RxBus

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun getSourceObservable(): Single<AccountResultWrapper> {
        val username = arguments?.getString(ARG_USERNAME)
        val password = arguments?.getString(ARG_PASSWORD)
        return mS1Service.login(username, password).map { resultWrapper ->
            // the authenticity token is not fresh after login
            resultWrapper.data.authenticityToken = null
            mUserValidator.validate(resultWrapper.data)
            resultWrapper
        }
    }

    override fun onNext(data: AccountResultWrapper) {
        val result = data.result
        if (result.status == STATUS_AUTH_SUCCESS || result.status == STATUS_AUTH_SUCCESS_ALREADY) {
            showShortTextAndFinishCurrentActivity(result.message)
            mRxBus.post(LoginEvent())
        } else {
            showToastText(result.message)
        }
    }

    override fun getProgressMessage(): CharSequence? {
        return getText(R.string.dialog_progress_message_login)
    }

    companion object {

        val TAG = LoginDialogFragment::class.java.name

        private val ARG_USERNAME = "username"
        private val ARG_PASSWORD = "password"

        /**
         * For desktop is "login_succeed".
         * For mobile is "location_login_succeed_mobile".
         * "login_succeed" when already has logged in.
         */
        private val STATUS_AUTH_SUCCESS = "location_login_succeed_mobile"
        private val STATUS_AUTH_SUCCESS_ALREADY = "login_succeed"


        fun newInstance(username: String, password: String): LoginDialogFragment {
            val fragment = LoginDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARG_USERNAME, username)
            bundle.putString(ARG_PASSWORD, password)
            fragment.arguments = bundle

            return fragment
        }
    }
}
