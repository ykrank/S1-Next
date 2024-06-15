package me.ykrank.s1next.view.page.setting.blacklist

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import me.ykrank.s1next.R
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.databinding.DialogBlacklistBinding
import me.ykrank.s1next.view.dialog.BaseDialogFragment
import me.ykrank.s1next.viewmodel.BlackDialogViewModel

/**
 * A dialog lets the user add or edit blacklist.
 */
class BlacklistDialogFragment : BaseDialogFragment() {

    private var mBlacklist: ArrayList<BlackList> = arrayListOf()
    private var mBinding: DialogBlacklistBinding? = null
    private var callBack: ((ArrayList<BlackList>) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val binding = DataBindingUtil.inflate<DialogBlacklistBinding>(
            activity.layoutInflater,
            R.layout.dialog_blacklist, null, false
        )
        mBinding = binding
        val blackDialogViewModel = BlackDialogViewModel()
        blackDialogViewModel.blacklist.clear()
        blackDialogViewModel.blacklist.addAll(mBlacklist)

        if (mBlacklist.size == 1) {
            binding.radioGroupForum.check(forumRadioIdFromFlag(mBlacklist[0]))
            binding.radioGroupPost.check(postRadioIdFromFlag(mBlacklist[0]))
        }
        binding.blackDialogViewModel = blackDialogViewModel


        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(if (mBlacklist.size == 0) R.string.menu_blacklist_add else R.string.menu_blacklist_edit)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                @BlackList.ForumFLag val forum = forumFlagFromRadioId()
                @BlackList.PostFLag val post = postFlagFromRadioId()
                if (mBlacklist.size == 0) {
                    val authorIds = binding.blacklistId.text.toString().trim { it <= ' ' }
                    val authorId =
                        if (TextUtils.isEmpty(authorIds)) 0 else Integer.parseInt(authorIds)
                    val authorName = binding.blacklistName.text.toString()
                    val blackList = BlackList(authorId, authorName, post, forum)
                    mBlacklist.add(blackList)
                }
                mBlacklist.forEach { blacklistItem ->
                    blacklistItem.post = post
                    blacklistItem.forum = forum
                }
                callBack?.invoke(mBlacklist)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        alertDialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )
        return alertDialog
    }

    @IdRes
    private fun forumRadioIdFromFlag(blackList: BlackList): Int {
        return when (blackList.forum) {
            BlackList.HIDE_FORUM -> R.id.radio_forum_hide
            BlackList.DEL_FORUM -> R.id.radio_forum_del
            else -> R.id.radio_forum_normal
        }
    }

    @BlackList.ForumFLag
    private fun forumFlagFromRadioId(): Int {
        return when (mBinding?.radioGroupForum?.checkedRadioButtonId) {
            R.id.radio_forum_hide -> BlackList.HIDE_FORUM
            R.id.radio_forum_del -> BlackList.DEL_FORUM
            else -> BlackList.NORMAL
        }
    }

    @IdRes
    private fun postRadioIdFromFlag(blackList: BlackList): Int {
        return when (blackList.post) {
            BlackList.HIDE_POST -> R.id.radio_post_hide
            BlackList.DEL_POST -> R.id.radio_post_del
            else -> R.id.radio_post_normal
        }
    }

    @BlackList.PostFLag
    private fun postFlagFromRadioId(): Int {
        return when (mBinding?.radioGroupPost?.checkedRadioButtonId) {
            R.id.radio_post_hide -> BlackList.HIDE_POST
            R.id.radio_post_del -> BlackList.DEL_POST
            else -> BlackList.NORMAL
        }
    }

    companion object {

        val BLACKLIST_TAG = "blacklist"
        val TAG = BlacklistDialogFragment::class.java.name

        fun newInstance(
            blackList: ArrayList<BlackList>,
            callBack: ((ArrayList<BlackList>) -> Unit)?
        ): BlacklistDialogFragment {
            val fragment = BlacklistDialogFragment()
            fragment.mBlacklist.addAll(blackList)
            fragment.callBack = callBack
            return fragment
        }
    }
}
