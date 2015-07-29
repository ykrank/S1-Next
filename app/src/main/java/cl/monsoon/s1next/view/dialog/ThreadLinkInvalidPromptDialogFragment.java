package cl.monsoon.s1next.view.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.util.IntentUtil;

/**
 * A dialog shows error prompt if the thread link is invalid.
 * Clicks the negative button can let user open this thread link in browser.
 */
public final class ThreadLinkInvalidPromptDialogFragment extends DialogFragment {

    public static final String TAG = ThreadLinkInvalidPromptDialogFragment.class.getName();

    private static final String ARG_MESSAGE = "message";

    public static ThreadLinkInvalidPromptDialogFragment newInstance(String message) {
        ThreadLinkInvalidPromptDialogFragment fragment = new ThreadLinkInvalidPromptDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MESSAGE, message);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static ThreadLinkInvalidPromptDialogFragment newInstance(Context context, @StringRes int message) {
        return newInstance(context.getString(message));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(getArguments().getString(ARG_MESSAGE))
                .setPositiveButton(R.string.dialog_button_text_done,
                        (dialog, which) -> dismiss())
                .setNegativeButton(R.string.dialog_button_text_use_a_different_app,
                        (dialog, which) ->
                                IntentUtil.startViewIntentExcludeOurApp(getActivity(),
                                        getActivity().getIntent().getData()))
                .create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        // getActivity() = null when configuration changes (like orientation changes)
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
