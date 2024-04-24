package me.ykrank.s1next.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.WindowManager
import com.github.ykrank.androidtools.util.ResourceUtil
import com.github.ykrank.androidtools.util.ViewUtil
import me.ykrank.s1next.R
import me.ykrank.s1next.data.db.biz.BlackWordBiz
import me.ykrank.s1next.data.db.dbmodel.BlackWord
import me.ykrank.s1next.databinding.DialogBlackWordBinding
import me.ykrank.s1next.view.internal.RequestCode
import me.ykrank.s1next.viewmodel.BlackWordViewModel

/**
 * A dialog lets the user add or edit blacklist.
 */
class BlackWordDialogFragment : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity!!
        val colorRight = ContextCompat.getColorStateList(
            activity,
            ResourceUtil.getResourceId(
                activity,
                com.github.ykrank.androidtools.R.attr.textColorRight
            )
        )
        val colorHint = ContextCompat.getColorStateList(
            activity,
            ResourceUtil.getResourceId(
                activity,
                com.github.ykrank.androidtools.R.attr.textColorHint
            )
        )

        val mBlackWord = arguments?.get(TAG_BLACK_WORD) as BlackWord?

        //Check could add
        if (mBlackWord == null) {
            if (BlackWordDbWrapper.instance.count() >= 10) {
                return AlertDialog.Builder(activity)
                    .setTitle(R.string.title_black_word_add)
                    .setMessage(R.string.error_word_out_of_bound)
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
            }
        }

        val binding = DataBindingUtil.inflate<DialogBlackWordBinding>(
            activity.layoutInflater,
            R.layout.dialog_black_word, null, false
        )
        val inputWord = binding.inputWord
        val etWord = binding.etWord
        val btnVerify = binding.btnVerify

        val model = BlackWordViewModel()
        if (mBlackWord != null)
            model.blackword.set(mBlackWord)
        binding.model = model

        //@array/black_list_stat_entry_values
        val stat = model.blackword.get()?.stat
        when (stat) {
            BlackWord.HIDE -> binding.spinner.setSelection(1)
            BlackWord.DEL -> binding.spinner.setSelection(2)
            else -> binding.spinner.setSelection(0)
        }

        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(if (mBlackWord == null) R.string.title_black_word_add else R.string.title_black_word_edit)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                val word = binding.etWord.text.toString().trim { it <= ' ' }
                val statSelectString = binding.spinner.selectedItemPosition

                //@array/black_list_stat_entry_values
                @BlackWord.BlackWordFLag val statSelect = when (statSelectString) {
                    1 -> BlackWord.HIDE
                    2 -> BlackWord.DEL
                    else -> BlackWord.NORMAL
                }
                val blackWord: BlackWord = mBlackWord ?: BlackWord(word, statSelect)
                blackWord.word = word
                blackWord.stat = statSelect
                val intent = Intent()
                intent.putExtra(TAG_BLACK_WORD, blackWord)
                targetFragment!!.onActivityResult(
                    RequestCode.REQUEST_CODE_BLACKLIST,
                    Activity.RESULT_OK,
                    intent
                )
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        alertDialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )

        // http://stackoverflow.com/a/7636468
        alertDialog.setOnShowListener { dialog ->
            val positionButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            ViewUtil.consumeRunnableWhenImeActionPerformed(etWord) {
                btnVerify.performClick()
            }

            btnVerify.setOnClickListener {
                val wordBlackWord = BlackWordDbWrapper.instance.getBlackWord(etWord.text.toString())
                if (wordBlackWord == null || (mBlackWord != null && wordBlackWord.id == mBlackWord.id)) {
                    positionButton.isEnabled = true
                    inputWord.helperText = "验证完成"
                    inputWord.setHelperTextColor(colorRight)
                } else {
                    positionButton.isEnabled = false
                    inputWord.error = resources.getText(R.string.error_duplicate_word)
                }
            }
            etWord.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    positionButton.isEnabled = false
                    inputWord.helperText = "需要验证"
                    inputWord.setHelperTextColor(colorHint)
                }
            })
            // check whether we need to disable position button when this dialog shows
            if (TextUtils.isEmpty(etWord.text)) {
                positionButton.isEnabled = false
            }
        }

        return alertDialog
    }

    companion object {

        val TAG_BLACK_WORD = "black_word"
        val TAG = BlackWordDialogFragment::class.java.name

        fun newInstance(blackWord: BlackWord?): BlackWordDialogFragment {
            val fragment = BlackWordDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(TAG_BLACK_WORD, blackWord)
            fragment.arguments = bundle

            return fragment
        }
    }
}
