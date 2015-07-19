package cl.monsoon.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.singleton.OkHttpClientProvider;

/**
 * A dialog shows logout prompt.
 * Logs out if user clicks the logout button.
 */
public final class LogoutDialogFragment extends DialogFragment {

    private static final String TAG = LogoutDialogFragment.class.getName();

    /**
     * Show {@link LogoutDialogFragment} if user has logged in.
     *
     * @return {@code true} if we need to show dialog, {@code false} otherwise.
     */
    public static boolean showLogoutDialogIfNeed(FragmentActivity fragmentActivity, User user) {
        if (user.isLogged()) {
            new LogoutDialogFragment().show(fragmentActivity.getSupportFragmentManager(),
                    LogoutDialogFragment.TAG);

            return true;
        }

        return false;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.dialog_message_log_out)
                .setPositiveButton(R.string.dialog_button_text_log_out, (dialog, which) -> logout())
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    /**
     * Clear user's cookies and current user's info.
     */
    private void logout() {
        OkHttpClientProvider.clearCookie();
        App.getAppComponent(getActivity()).getUser().setLogged(false);
    }
}
