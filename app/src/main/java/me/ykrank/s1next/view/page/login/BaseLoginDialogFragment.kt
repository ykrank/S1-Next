package me.ykrank.s1next.view.page.login

import android.os.Bundle
import com.github.ykrank.androidtools.widget.RxBus
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.app.AppService
import me.ykrank.s1next.data.api.app.model.AppDataWrapper
import me.ykrank.s1next.data.api.app.model.AppLoginResult
import me.ykrank.s1next.view.dialog.ProgressDialogFragment
import me.ykrank.s1next.view.event.AppLoginEvent
import javax.inject.Inject

/**
 * A [ProgressDialogFragment] posts a request to login to server.
 */
abstract class BaseLoginDialogFragment<T> : ProgressDialogFragment<T>() {

    protected var mRxBus: RxBus? = null

    protected var username: String? = null
    protected var password: String? = null
    protected var questionId: Int? = null
    protected var answer: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        mRxBus = App.appComponent.preAppComponent.rxBus
        super.onCreate(savedInstanceState)

        username = arguments?.getString(ARG_USERNAME)
        password = arguments?.getString(ARG_PASSWORD)
        questionId = arguments?.getInt(ARG_QUESTION_ID)
        answer = arguments?.getString(ARG_ANSWER)
    }

    override fun onNext(data: T) {
        val result = parseData(data)
        if (result.isSuccess) {
            showShortTextAndFinishCurrentActivity(result.message)
            mRxBus?.post(AppLoginEvent())
        } else {
            showToastText(result.message)
        }
    }

    abstract fun parseData(data: T): Result

    override fun getProgressMessage(): CharSequence? {
        return getText(R.string.dialog_progress_message_login)
    }

    data class Result(val isSuccess: Boolean, val message: String?)

    companion object {

        val TAG: String = BaseLoginDialogFragment::class.java.name

        private const val ARG_USERNAME = "username"
        private const val ARG_PASSWORD = "password"
        private const val ARG_QUESTION_ID = "question_id"
        private const val ARG_ANSWER = "answer"

        fun addBundle(
            bundle: Bundle,
            username: String,
            password: String,
            questionId: Int?,
            answer: String?
        ): Bundle {
            bundle.putString(ARG_USERNAME, username)
            bundle.putString(ARG_PASSWORD, password)
            questionId?.let { bundle.putInt(ARG_QUESTION_ID, it) }
            bundle.putString(ARG_ANSWER, answer)
            return bundle
        }
    }
}
