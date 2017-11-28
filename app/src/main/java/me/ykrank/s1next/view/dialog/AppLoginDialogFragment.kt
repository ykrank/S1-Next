package me.ykrank.s1next.view.dialog

import android.os.Bundle
import com.github.ykrank.androidtools.widget.RxBus
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.app.AppService
import me.ykrank.s1next.data.api.app.model.AppDataWrapper
import me.ykrank.s1next.data.api.app.model.AppLoginResult
import me.ykrank.s1next.view.event.AppLoginEvent
import javax.inject.Inject

/**
 * A [ProgressDialogFragment] posts a request to login to server.
 */
class AppLoginDialogFragment : ProgressDialogFragment<AppDataWrapper<AppLoginResult>>() {

    @Inject
    internal lateinit var mAppService: AppService
    @Inject
    internal lateinit var mRxBus: RxBus

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun getSourceObservable(): Single<AppDataWrapper<AppLoginResult>> {
        val username = arguments.getString(ARG_USERNAME)
        val password = arguments.getString(ARG_PASSWORD)
        val questionId = arguments.getInt(ARG_QUESTION_ID)
        val answer = arguments.getString(ARG_ANSWER)
        return mAppService.login(username, password, questionId, answer)
    }

    override fun onNext(data: AppDataWrapper<AppLoginResult>) {
        if (data.success) {
            if (mUserValidator.validateAppLoginInfo(data.data)) {
                showShortTextAndFinishCurrentActivity(data.message)
                mRxBus.post(AppLoginEvent())
            } else {
                showToastText(getString(R.string.app_login_info_error))
            }
        } else {
            showToastText(data.message)
        }
    }

    override fun getProgressMessage(): CharSequence? {
        return getText(R.string.dialog_progress_message_login)
    }

    companion object {

        val TAG: String = AppLoginDialogFragment::class.java.name

        private val ARG_USERNAME = "username"
        private val ARG_PASSWORD = "password"
        private val ARG_QUESTION_ID = "question_id"
        private val ARG_ANSWER = "answer"

        fun newInstance(username: String, password: String, questionId: Int, answer: String): AppLoginDialogFragment {
            val fragment = AppLoginDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARG_USERNAME, username)
            bundle.putString(ARG_PASSWORD, password)
            bundle.putInt(ARG_QUESTION_ID, questionId)
            bundle.putString(ARG_ANSWER, answer)
            fragment.arguments = bundle

            return fragment
        }
    }
}
