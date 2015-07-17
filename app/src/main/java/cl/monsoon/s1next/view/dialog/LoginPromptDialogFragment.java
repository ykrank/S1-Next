package cl.monsoon.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.view.activity.LoginActivity;

/**
 * A dialog shows login prompt.
 */
public final class LoginPromptDialogFragment extends DialogFragment {

    private static final String TAG = LoginPromptDialogFragment.class.getName();

    /**
     * Show {@link LoginPromptDialogFragment} if user hasn't logged in.
     *
     * @return whether need to show dialog
     */
    public static boolean showLoginPromptDialogIfNeed(FragmentActivity fragmentActivity, User user) {
        if (!user.isLogged()) {
            new LoginPromptDialogFragment().show(fragmentActivity.getSupportFragmentManager(),
                    LoginPromptDialogFragment.TAG);

            return true;
        }

        return false;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.dialog_message_login_prompt)
                .setPositiveButton(R.string.action_login,
                        (dialog, which) -> {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
