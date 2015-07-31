package cl.monsoon.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import cl.monsoon.s1next.R;

/**
 * A dialog shows prompt if user's reply is not empty and want
 * to finish current Activity.
 */
public final class ReplyDiscardPromptDialogFragment extends DialogFragment {

    public static final String TAG = ReplyDiscardPromptDialogFragment.class.getName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.dialog_message_reply_discard_prompt)
                .setPositiveButton(R.string.dialog_message_text_discard, (dialog, which) ->
                        getActivity().finish())
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
