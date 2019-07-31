package me.ykrank.s1next.view.dialog;

import android.app.Dialog;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import me.ykrank.s1next.R;
import me.ykrank.s1next.databinding.DialogVersionInfoBinding;

/**
 * A dialog shows version info.
 */
public final class VersionInfoDialogFragment extends BaseDialogFragment {

    public static final String TAG = VersionInfoDialogFragment.class.getName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogVersionInfoBinding binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(),
                R.layout.dialog_version_info, null, false);

        return new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .create();
    }
}
