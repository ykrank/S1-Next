package me.ykrank.s1next.view.page.login

import android.os.Bundle
import com.github.ykrank.androidtools.widget.RxBus
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper
import me.ykrank.s1next.view.dialog.ProgressDialogFragment
import me.ykrank.s1next.view.event.LoginEvent
import javax.inject.Inject

/**
 * A [ProgressDialogFragment] posts a request to login to server.
 */
class LoginDialogFragment : BaseLoginDialogFragment<AccountResultWrapper>() {

    override fun getSourceObservable(): Single<AccountResultWrapper> {
        return mS1Service.login(username, password, questionId, answer).map { resultWrapper ->
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
            mRxBus?.post(LoginEvent())
        } else {
            showToastText(result.message)
        }
    }

    override fun parseData(data: AccountResultWrapper): Result {
        val result = data.result
        return if (result.status == STATUS_AUTH_SUCCESS || result.status == STATUS_AUTH_SUCCESS_ALREADY) {
            Result(true, result.message)
        } else {
            Result(false, result.message)
        }
    }

    companion object {

        val TAG = LoginDialogFragment::class.java.name

        /**
         * For desktop is "login_succeed".
         * For mobile is "location_login_succeed_mobile".
         * "login_succeed" when already has logged in.
         */
        private const val STATUS_AUTH_SUCCESS = "location_login_succeed_mobile"
        private const val STATUS_AUTH_SUCCESS_ALREADY = "login_succeed"


        fun newInstance(
            username: String,
            password: String,
            questionId: Int?,
            answer: String?
        ): LoginDialogFragment {
            val fragment = LoginDialogFragment()
            val bundle = Bundle()
            addBundle(bundle, username, password, questionId, answer)
            fragment.arguments = bundle

            return fragment
        }
    }
}
