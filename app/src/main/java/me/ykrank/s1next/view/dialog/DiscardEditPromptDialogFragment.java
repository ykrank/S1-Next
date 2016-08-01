package me.ykrank.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import me.ykrank.s1next.R;

/**
 * A dialog shows prompt if user's reply is not empty and want
 * to finish current Activity.
 */
public final class DiscardEditPromptDialogFragment extends DialogFragment {

    public static final String TAG = DiscardEditPromptDialogFragment.class.getName();

    private static final String ARG_MESSAGE = "message";

    public static DiscardEditPromptDialogFragment newInstance(String msg) {
        DiscardEditPromptDialogFragment fragment = new DiscardEditPromptDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MESSAGE, msg);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String msg = getArguments().getString(ARG_MESSAGE);
        if (msg == null) msg = getString(R.string.dialog_message_reply_discard_prompt);

        return new AlertDialog.Builder(getContext())
                .setMessage(msg)
                .setPositiveButton(R.string.dialog_message_text_discard, (dialog, which) ->
                        getActivity().finish())
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
