package me.ykrank.s1next.view.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.github.ykrank.androidtools.util.ViewUtil
import com.google.common.base.Optional
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.ThreadLink
import me.ykrank.s1next.databinding.DialogThreadGoBinding
import me.ykrank.s1next.view.page.post.PostListActivity

/**
 * A dialog lets the user enter thread link/ID to go to that thread.
 */
class ThreadGoDialogFragment : BaseDialogFragment() {

    private var mThreadLink: Optional<ThreadLink> = Optional.absent()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity!!
        val binding = DataBindingUtil.inflate<DialogThreadGoBinding>(activity.layoutInflater,
                R.layout.dialog_thread_go, null, false)
        val threadLinkOrIdWrapperView = binding.threadLinkOrIdWrapper
        val threadLinkOrIdView = binding.threadLinkOrId

        val alertDialog = AlertDialog.Builder(activity)
                .setTitle(R.string.menu_thread_go)
                .setView(binding.root)
                .setPositiveButton(R.string.dialog_button_text_go) { dialog, which ->
                    run {
                        if (mThreadLink.isPresent) {
                            PostListActivity.start(activity, mThreadLink.get(), false)
                        }
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        alertDialog.window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        // http://stackoverflow.com/a/7636468
        alertDialog.setOnShowListener { dialog ->
            val positionButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            ViewUtil.consumeRunnableWhenImeActionPerformed(threadLinkOrIdView) {
                if (positionButton.isEnabled) {
                    positionButton.performClick()
                } else {
                    if (threadLinkOrIdWrapperView.error == null) {
                        threadLinkOrIdWrapperView.error = resources.getText(
                                R.string.error_field_invalid_or_unsupported_thread_link_or_id)
                    }
                }
            }

            threadLinkOrIdView.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    val threadLinkOrId = s.toString()
                    if (!TextUtils.isEmpty(threadLinkOrId)) {
                        mThreadLink = ThreadLink.parse2(threadLinkOrId)
                        if (mThreadLink.isPresent) {
                            threadLinkOrIdWrapperView.error = null
                            positionButton.isEnabled = true
                        } else {
                            if (threadLinkOrIdWrapperView.error == null) {
                                threadLinkOrIdWrapperView.error = resources.getText(
                                        R.string.error_field_invalid_or_unsupported_thread_link_or_id)
                            }
                            positionButton.isEnabled = false
                        }
                    }
                }
            })
            // check whether we need to disable position button when this dialog shows
            if (TextUtils.isEmpty(threadLinkOrIdView.text)) {
                positionButton.isEnabled = false
            } else {
                threadLinkOrIdView.text = threadLinkOrIdView.text
            }
        }
        return alertDialog
    }

    companion object {

        val TAG = ThreadGoDialogFragment::class.java.name
    }
}
