package me.ykrank.s1next.view.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import me.ykrank.s1next.R
import me.ykrank.s1next.databinding.DialogLoadProgressBinding


/**
 * 有进度的dialog
 */
open class BaseLoadProgressDialogFragment : BaseDialogFragment() {

    lateinit var binding: DialogLoadProgressBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLoadProgressBinding.inflate(inflater, container, false)
        isCancelable = false

        binding.btnStop.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.stop_confirm)
                .setPositiveButton(R.string.stop) { _: DialogInterface, _: Int ->
                    dismiss()
                }.setNegativeButton(android.R.string.cancel) { _: DialogInterface, _: Int ->
                }
                .show()
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        val TAG: String = BaseLoadProgressDialogFragment::class.java.name
    }
}
