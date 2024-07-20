package me.ykrank.s1next.view.page.setting.blacklist

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.ViewUtil
import com.github.ykrank.androidtools.widget.RxBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ykrank.s1next.App.Companion.appComponent
import me.ykrank.s1next.R
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.databinding.DialogBlacklistRemarkBinding
import me.ykrank.s1next.view.dialog.BaseDialogFragment
import me.ykrank.s1next.view.event.BlackListChangeEvent
import me.ykrank.s1next.widget.track.event.BlackListTrackEvent
import javax.inject.Inject

/**
 * A dialog lets the user enter blacklist remark.
 */
class BlackListRemarkDialogFragment : BaseDialogFragment() {
    @Inject
    lateinit var rxBus: RxBus

    @Inject
    lateinit var blackListDb: BlackListBiz

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        appComponent.inject(this)

        val activity: Activity = requireActivity()
        val binding = DataBindingUtil.inflate<DialogBlacklistRemarkBinding>(
            activity.layoutInflater,
            R.layout.dialog_blacklist_remark, null, false
        )

        val arguments = requireArguments()
        val alertDialog = AlertDialog.Builder(
            activity
        )
            .setTitle(R.string.menu_blacklist_add)
            .setView(binding.root)
            .setPositiveButton(R.string.dialog_button_text_confirm) { dialog: DialogInterface?, which: Int ->
                val authorId = arguments.getInt(
                    ARG_AUTHOR_ID
                )
                val authorName = arguments.getString(ARG_AUTHOR_NAME)
                val remark = binding.blacklistRemark.text.toString()
                trackAgent?.post(BlackListTrackEvent(true, authorId.toString(), authorName))

                lifecycleScope.launch(L.report) {
                    withContext(Dispatchers.IO) {
                        blackListDb.saveDefaultBlackList(authorId, authorName, remark)
                    }
                    rxBus.post(BlackListChangeEvent(authorId, authorName, remark, true))
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        alertDialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )
        ViewUtil.consumeRunnableWhenImeActionPerformed(binding.blacklistRemark) {
            alertDialog.getButton(
                DialogInterface.BUTTON_POSITIVE
            ).performClick()
        }
        return alertDialog
    }

    companion object {
        @JvmField
        val TAG: String = BlackListRemarkDialogFragment::class.java.name

        private const val ARG_AUTHOR_ID = "arg_author_id"
        private const val ARG_AUTHOR_NAME = "arg_author_name"

        fun newInstance(authorId: Int, authorName: String?): BlackListRemarkDialogFragment {
            val fragment = BlackListRemarkDialogFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_AUTHOR_ID, authorId)
            bundle.putString(ARG_AUTHOR_NAME, authorName)
            fragment.arguments = bundle

            return fragment
        }
    }
}
