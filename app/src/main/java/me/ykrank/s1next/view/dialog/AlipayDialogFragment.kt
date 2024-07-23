package me.ykrank.s1next.view.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.github.ykrank.androidtools.extension.toast
import com.github.ykrank.androidtools.util.ClipboardUtil
import com.github.ykrank.androidtools.widget.AlipayDonate
import me.ykrank.s1next.R
import me.ykrank.s1next.databinding.DialogAlipayBinding
import me.ykrank.s1next.util.BuglyUtils

/**
 * A dialog lets the user enter blacklist remark.
 */
class AlipayDialogFragment : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity as Activity
        val binding = DataBindingUtil.inflate<DialogAlipayBinding>(
            activity.layoutInflater,
            R.layout.dialog_alipay, null, false
        )

        val title = arguments?.getString(ARG_TITLE)
        val msg = arguments?.getString(ARG_MESSAGE)

        val clickListener = {
            ClipboardUtil.copyText(activity, title, msg)
            activity.toast(R.string.copied)
        }

        binding.tvCode.text = msg
        binding.root.setOnClickListener { clickListener.invoke() }

        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(binding.root)
            .setPositiveButton(R.string.dialog_button_text_confirm) { dialog, which ->
                clickListener.invoke()
                if (AlipayDonate.hasInstalledAlipayClient(activity)) {
                    AlipayDonate.openAlipaySearch(activity, msg?:"")
                } else {
                    if (!BuglyUtils.isPlay) {
                        activity.toast("或许需要手动打开支付宝首页搜索")
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        return alertDialog
    }

    companion object {

        val TAG = AlipayDialogFragment::class.java.simpleName

        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"

        fun newInstance(title: String, msg: String): AlipayDialogFragment {
            val fragment = AlipayDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARG_TITLE, title)
            bundle.putString(ARG_MESSAGE, msg)
            fragment.arguments = bundle
            return fragment
        }
    }
}
