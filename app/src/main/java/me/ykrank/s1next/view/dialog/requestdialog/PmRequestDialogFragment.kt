package me.ykrank.s1next.view.dialog.requestdialog

import android.os.Bundle
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper
import me.ykrank.s1next.widget.track.event.NewPmTrackEvent

/**
 * A dialog requests to post pm.
 */
class PmRequestDialogFragment : BaseRequestDialogFragment<AccountResultWrapper>() {

    override fun getProgressMessage(): CharSequence? {
        return getText(R.string.dialog_progress_message_reply)
    }

    override fun getSourceObservable(): Single<AccountResultWrapper> {
        val bundle = arguments!!
        val toUid = bundle.getString(ARG_TO_UID)
        val msg = bundle.getString(ARG_MESSAGE)

        return flatMappedWithAuthenticityToken { token -> mS1Service.postPm(token, toUid, msg) }
    }

    override fun onNext(data: AccountResultWrapper) {
        val result = data.result
        if (result.status == STATUS_PM_SUCCESS) {
            onRequestSuccess(result.message)
        } else {
            onRequestError(result.message)
        }
    }

    companion object {

        val TAG: String = PmRequestDialogFragment::class.java.name

        private val ARG_TO_UID = "arg_to_uid"
        private val ARG_MESSAGE = "message"

        private val STATUS_PM_SUCCESS = "do_success"

        fun newInstance(toUid: String, msg: String): PmRequestDialogFragment {
            App.get().trackAgent.post(NewPmTrackEvent())

            val fragment = PmRequestDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARG_TO_UID, toUid)
            bundle.putString(ARG_MESSAGE, msg)
            fragment.arguments = bundle

            return fragment
        }
    }
}
