package me.ykrank.s1next.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.ViewUtil
import com.github.ykrank.androidtools.widget.AppException
import me.ykrank.s1next.R
import me.ykrank.s1next.databinding.DialogReportErrorBinding

/**
 * Created by ykrank on 2017/10/16.
 */
class ReportErrorDialogFragment : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DataBindingUtil.inflate<DialogReportErrorBinding>(activity.layoutInflater,
                R.layout.dialog_report_error, null, false)
        val alertDialog = AlertDialog.Builder(activity)
                .setTitle(R.string.menu_send_report)
                .setView(binding.root)
                .setPositiveButton(R.string.dialog_report) { dialog, which -> L.report(binding.etErrorMsg.text.toString(), AppException()) }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        alertDialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        // http://stackoverflow.com/a/7636468
        alertDialog.setOnShowListener { dialog ->
            val positionButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            ViewUtil.consumeRunnableWhenImeActionPerformed(binding.etErrorMsg) {
                if (positionButton.isEnabled) {
                    positionButton.performClick()
                }
            }

            binding.etErrorMsg.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    positionButton.isEnabled = !s.isNullOrBlank()
                }
            })
            // check whether we need to disable position button when this dialog shows
            positionButton.isEnabled = !binding.etErrorMsg.text.isNullOrBlank()
        }
        return alertDialog
    }
}