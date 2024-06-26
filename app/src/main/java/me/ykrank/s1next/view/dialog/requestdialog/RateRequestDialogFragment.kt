package me.ykrank.s1next.view.dialog.requestdialog

import android.os.Bundle
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.AjaxResult
import me.ykrank.s1next.data.api.model.RatePreInfo
import me.ykrank.s1next.view.dialog.ProgressDialogFragment
import me.ykrank.s1next.widget.track.event.NewRateTrackEvent

/**
 * A dialog requests to reply to post.
 */
class RateRequestDialogFragment : ProgressDialogFragment<AjaxResult>() {

    override fun getProgressMessage(): CharSequence? {
        return getText(R.string.dialog_progress_message_reply)
    }

    override fun getSourceObservable(): Single<AjaxResult> {
        val bundle = requireArguments()
        val ratePreInfo = bundle.getParcelable<RatePreInfo>(ARG_RATE_PRE_INFO)
        val score = bundle.getString(ARG_SCORE)
        val reason = bundle.getString(ARG_REASON)
        if (ratePreInfo == null) {
            return Single.error<AjaxResult>(IllegalStateException("RatePreInfo is null"))
        }
        return mS1Service.rate(ratePreInfo.formHash, ratePreInfo.tid, ratePreInfo.pid,
                ratePreInfo.refer, ratePreInfo.handleKey, score, reason)
            .map { AjaxResult.fromAjaxString(it) }
    }

    override fun onNext(data: AjaxResult) {
        if (data.success) {
            showShortTextAndFinishCurrentActivity(getString(R.string.rate_success))
        } else {
            showToastText(data.msg)
        }
    }

    companion object {

        val TAG: String = RateRequestDialogFragment::class.java.name

        private const val ARG_RATE_PRE_INFO = "rate_pre_info"
        private const val ARG_SCORE = "score"
        private const val ARG_REASON = "reason"

        fun newInstance(ratePreInfo: RatePreInfo, score: String, reason: String): RateRequestDialogFragment {
            App.get().trackAgent.post(NewRateTrackEvent(ratePreInfo.tid, ratePreInfo.pid, score, reason))

            val fragment = RateRequestDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_RATE_PRE_INFO, ratePreInfo)
            bundle.putString(ARG_SCORE, score)
            bundle.putString(ARG_REASON, reason)
            fragment.arguments = bundle

            return fragment
        }
    }
}
