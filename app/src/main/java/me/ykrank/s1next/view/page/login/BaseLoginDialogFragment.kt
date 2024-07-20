package me.ykrank.s1next.view.page.login

import android.os.Bundle
import com.github.ykrank.androidtools.widget.EventBus
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.view.dialog.ProgressDialogFragment

/**
 * A [ProgressDialogFragment] posts a request to login to server.
 */
abstract class BaseLoginDialogFragment<T> : ProgressDialogFragment<T>() {

    protected var mEventBus: EventBus? = null

    protected var username: String? = null
    protected var password: String? = null
    protected var questionId: Int? = null
    protected var answer: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        mEventBus = App.appComponent.preAppComponent.eventBus
        super.onCreate(savedInstanceState)

        username = arguments?.getString(ARG_USERNAME)
        password = arguments?.getString(ARG_PASSWORD)
        questionId = arguments?.getInt(ARG_QUESTION_ID)
        answer = arguments?.getString(ARG_ANSWER)
    }

    override fun onNext(data: T) {
        val result = parseData(data)
        if (result.isSuccess) {
            onSuccess(data, result)
        } else {
            showToastText(result.message)
        }
    }

    open fun onSuccess(data: T, result: Result) {
        showShortTextAndFinishCurrentActivity(result.message)
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
