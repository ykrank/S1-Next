package me.ykrank.s1next.view.dialog

import android.app.Dialog
import android.content.DialogInterface
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.WindowManager

import com.github.ykrank.androidtools.util.ViewUtil

import me.ykrank.s1next.R
import me.ykrank.s1next.databinding.DialogFavouritesAddBinding

/**
 * A dialog lets user enter remark if user want to add this thread to his/her favourites.
 * Clicks the positive button can let user add this thread to his/her favourites.
 */
class ThreadFavouritesAddDialogFragment : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity!!

        val binding = DataBindingUtil.inflate<DialogFavouritesAddBinding>(activity.layoutInflater,
                R.layout.dialog_favourites_add, null, false)

        val alertDialog = AlertDialog.Builder(activity)
                .setTitle(R.string.menu_favourites_add)
                .setView(binding.root)
                .setPositiveButton(R.string.dialog_button_text_add, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        alertDialog.window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        // http://stackoverflow.com/a/7636468
        alertDialog.setOnShowListener { dialog ->
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { v ->
                val threadId = arguments?.getString(ARG_THREAD_ID)
                if (!threadId.isNullOrEmpty()) {
                    ThreadFavouritesAddRequestDialogFragment.newInstance(threadId,
                            binding.remark.text.toString(), arguments?.getString(ARG_THREAD_TITLE))
                            .show(fragmentManager!!, ThreadFavouritesAddRequestDialogFragment.TAG)
                }
            }
            ViewUtil.consumeRunnableWhenImeActionPerformed(binding.remark) { alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick() }
        }
        return alertDialog
    }

    companion object {

        val TAG = ThreadFavouritesAddDialogFragment::class.java.name

        private const val ARG_THREAD_ID = "thread_id"
        private const val ARG_THREAD_TITLE = "thread_title"

        fun newInstance(threadId: String, threadTitle: String?): ThreadFavouritesAddDialogFragment {
            val fragment = ThreadFavouritesAddDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARG_THREAD_ID, threadId)
            bundle.putString(ARG_THREAD_TITLE, threadTitle)
            fragment.arguments = bundle

            return fragment
        }
    }
}
