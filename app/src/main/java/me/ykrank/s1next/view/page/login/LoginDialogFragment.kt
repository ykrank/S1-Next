package me.ykrank.s1next.view.page.login

import android.os.Bundle
import io.reactivex.Single
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ykrank.s1next.App
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper
import me.ykrank.s1next.data.db.exmodel.RealLoginUser
import me.ykrank.s1next.view.dialog.ProgressDialogFragment
import me.ykrank.s1next.view.event.LoginEvent

/**
 * A [ProgressDialogFragment] posts a request to login to server.
 */
class LoginDialogFragment : BaseLoginDialogFragment<AccountResultWrapper>() {

    override fun getSourceObservable(): Single<AccountResultWrapper> {
        return mS1Service.login(username, password, questionId, answer).map { resultWrapper ->
            // the authenticity token is not fresh after login
            resultWrapper.data?.apply {
                authenticityToken = null
                mUserValidator.validate(this)
            }
            resultWrapper
        }
    }

    override fun parseData(data: AccountResultWrapper): Result {
        val result = data.result
        return if (result.status?.endsWith(STATUS_AUTH_SUCCESS) == true || result.defaultSuccess) {
            Result(true, result.message)
        } else {
            Result(false, result.message)
        }
    }

    override fun onSuccess(data: AccountResultWrapper, result: Result) {
        super.onSuccess(data, result)

        // 自动登录黑科技
        val username = this.username
        val password = this.password
        if (username != null && password != null) {
            saveLoginUser2Db(data)

            AppLoginDialogFragment.newInstance(username, password, questionId, answer).show(
                parentFragmentManager,
                AppLoginDialogFragment.TAG
            )
        }

        mEventBus?.post(LoginEvent())
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveLoginUser2Db(data: AccountResultWrapper) {
        GlobalScope.launch(Dispatchers.IO) {
            val time = System.currentTimeMillis()
            val user = RealLoginUser(
                id = null,
                uid = data.data?.uid?.toInt() ?: 0,
                name = data.data?.username,
                password = password,
                questionId = questionId?.toString(),
                answer = answer,
                loginTime = time,
                timestamp = time,
            )
            App.appComponent.loginUserBiz.saveUser(user)
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
