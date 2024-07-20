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
class AppLoginDialogFragment : BaseLoginDialogFragment<AppDataWrapper<AppLoginResult>>() {

    @Inject
    internal lateinit var mAppService: AppService

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun getSourceObservable(): Single<AppDataWrapper<AppLoginResult>> {
        return mAppService.login(username, password, questionId, answer)
    }

    override fun parseData(data: AppDataWrapper<AppLoginResult>): Result {
        return if (data.success) {
            if (mUserValidator.validateAppLoginInfo(data.data)) {
                Result(true, data.message)
            } else {
                Result(false, getString(R.string.app_login_info_error))
            }
        } else {
            Result(false, data.message)
        }
    }

    override fun onSuccess(data: AppDataWrapper<AppLoginResult>, result: Result) {
        super.onSuccess(data, result)
        mRxBus?.post(AppLoginEvent())
    }

    companion object {

        val TAG: String = AppLoginDialogFragment::class.java.name

        fun newInstance(
            username: String,
            password: String,
            questionId: Int?,
            answer: String?
        ): AppLoginDialogFragment {
            val fragment = AppLoginDialogFragment()
            val bundle = Bundle()
            addBundle(bundle, username, password, questionId, answer)
            fragment.arguments = bundle

            return fragment
        }
    }
}
