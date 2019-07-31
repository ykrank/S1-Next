package me.ykrank.s1next.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.text.TextUtils
import android.view.WindowManager

import me.ykrank.s1next.R
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.databinding.DialogBlacklistBinding
import me.ykrank.s1next.view.internal.RequestCode
import me.ykrank.s1next.viewmodel.BlackListViewModel

/**
 * A dialog lets the user add or edit blacklist.
 */
class BlacklistDialogFragment : BaseDialogFragment() {

    private var mBlacklist: BlackList? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity!!
        val blacklist = arguments?.get(BLACKLIST_TAG)
        if (blacklist != null) {
            mBlacklist = blacklist as BlackList
        }

        val binding = DataBindingUtil.inflate<DialogBlacklistBinding>(activity.layoutInflater,
                R.layout.dialog_blacklist, null, false)
        val blackListViewModel = BlackListViewModel()
        if (mBlacklist != null)
            blackListViewModel.blacklist.set(mBlacklist)
        binding.blackListViewModel = blackListViewModel

        val alertDialog = AlertDialog.Builder(activity)
                .setTitle(if (mBlacklist == null) R.string.menu_blacklist_add else R.string.menu_blacklist_edit)
                .setView(binding.root)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    val authorIds = binding.blacklistId.text.toString().trim { it <= ' ' }
                    val authorId = if (TextUtils.isEmpty(authorIds)) 0 else Integer.parseInt(authorIds)
                    val authorName = binding.blacklistName.text.toString()
                    @BlackList.ForumFLag val forum = if (binding.switchForum.isChecked) BlackList.HIDE_FORUM else BlackList.NORMAL
                    @BlackList.PostFLag val post = if (binding.switchPost.isChecked) BlackList.HIDE_POST else BlackList.NORMAL
                    val blackList = BlackList(authorId, authorName, post, forum)
                    val intent = Intent()
                    intent.putExtra(BLACKLIST_TAG, blackList)
                    targetFragment?.onActivityResult(RequestCode.REQUEST_CODE_BLACKLIST, Activity.RESULT_OK, intent)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        alertDialog.window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return alertDialog
    }

    companion object {

        val BLACKLIST_TAG = "blacklist"
        val TAG = BlacklistDialogFragment::class.java.name

        fun newInstance(blackList: BlackList?): BlacklistDialogFragment {
            val fragment = BlacklistDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(BLACKLIST_TAG, blackList)
            fragment.arguments = bundle

            return fragment
        }
    }
}
