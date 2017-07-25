package me.ykrank.s1next.view.dialog

import android.os.Bundle
import android.support.v4.app.FragmentManager

import io.reactivex.Observable

/**
 * A [ProgressDialogFragment] subscribe a observable
 */
class SimpleSupportProgressDialogFragment : ProgressDialogFragment<Any>() {

    private var progressMsg: CharSequence? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        progressMsg = arguments.getCharSequence(ARG_PROGRESS_MSG)
        super.onCreate(savedInstanceState)
    }

    override fun getSourceObservable(): Observable<Any> {
        return Observable.empty()
    }

    override fun onNext(data: Any) {

    }

    override fun getProgressMessage(): CharSequence? {
        return progressMsg
    }

    companion object {

        val TAG: String = SimpleSupportProgressDialogFragment::class.java.name

        private val ARG_PROGRESS_MSG = "progress_msg"

        /**
         * start a progress dialog to subscribe a observable and show msg (or not) after subscribed

         * @param progressMsg Message show in progress dialog
         * *
         * @param cancelable  whether dialog cancelable on touch outside
         */
        fun start(fm: FragmentManager, progressMsg: CharSequence?, cancelable: Boolean): SimpleSupportProgressDialogFragment {
            val fragment = SimpleSupportProgressDialogFragment()
            val bundle = Bundle()
            bundle.putCharSequence(ARG_PROGRESS_MSG, progressMsg)
            bundle.putBoolean(ProgressDialogFragment.ARG_DIALOG_NOT_CANCELABLE_ON_TOUCH_OUTSIDE, !cancelable)
            fragment.arguments = bundle

            fragment.show(fm, SimpleSupportProgressDialogFragment.TAG)

            return fragment
        }
    }


}
