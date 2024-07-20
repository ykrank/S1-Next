package me.ykrank.s1next.view.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.databinding.DialogVersionInfoBinding

/**
 * A dialog shows version info.
 */
class VersionInfoDialogFragment : BaseDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DataBindingUtil.inflate<DialogVersionInfoBinding>(
            requireActivity().layoutInflater,
            R.layout.dialog_version_info, null, false
        )

        binding.dbVersion = App.appComponent.appDatabaseManager.version.toString()
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    companion object {
        val TAG: String = VersionInfoDialogFragment::class.java.name
    }
}
