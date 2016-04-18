package me.ykrank.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import me.ykrank.s1next.R;
import me.ykrank.s1next.databinding.DialogVersionInfoBinding;

/**
 * A dialog shows version info.
 */
public final class VersionInfoDialogFragment extends DialogFragment {

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
