package me.ykrank.s1next.view.dialog

import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.os.Bundle
import androidx.annotation.CallSuper

/**
 * A [ProgressDialogFragment] subscribe a observable
 */
class SimpleProgressDialogFragment : DialogFragment() {

    private var progressMsg: CharSequence? = null
    private var dialogNotCancelableOnTouchOutside: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogNotCancelableOnTouchOutside = arguments.getBoolean(ARG_DIALOG_NOT_CANCELABLE_ON_TOUCH_OUTSIDE, false)
        progressMsg = arguments.getCharSequence(ARG_PROGRESS_MSG)

        // retain this Fragment
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(progressMsg)
        //press back will remove this fragment, so set cancelable no effect
        progressDialog.setCanceledOnTouchOutside(!dialogNotCancelableOnTouchOutside)

        return progressDialog
    }

    @CallSuper
    override fun onDestroyView() {
        // see https://code.google.com/p/android/issues/detail?id=17423
        val dialog = dialog
        if (dialog != null) {
            getDialog().setOnDismissListener(null)
        }

        super.onDestroyView()
    }

    companion object {
        val TAG: String = SimpleProgressDialogFragment::class.java.simpleName
        private const val ARG_DIALOG_NOT_CANCELABLE_ON_TOUCH_OUTSIDE = "dialog_not_cancelable_on_touch_outside"

        private const val ARG_PROGRESS_MSG = "progress_msg"

        /**
         * start a progress dialog to subscribe a observable and show msg (or not) after subscribed

         * @param progressMsg Message show in progress dialog
         * *
         * @param cancelable  whether dialog cancelable on touch outside
         */
        fun start(fm: FragmentManager, progressMsg: CharSequence? = null, cancelable: Boolean = false): SimpleProgressDialogFragment {
            val fragment = SimpleProgressDialogFragment()
            val bundle = Bundle()
            bundle.putCharSequence(ARG_PROGRESS_MSG, progressMsg)
            bundle.putBoolean(ARG_DIALOG_NOT_CANCELABLE_ON_TOUCH_OUTSIDE, !cancelable)
            fragment.arguments = bundle

            fragment.show(fm, SimpleProgressDialogFragment.TAG)

            return fragment
        }
    }
}
