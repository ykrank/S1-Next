package me.ykrank.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.view.activity.LoginActivity;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * A dialog shows login prompt.
 */
public final class LoginPromptDialogFragment extends BaseDialogFragment {

    private static final String TAG = LoginPromptDialogFragment.class.getName();

    /**
     * Show {@link LoginPromptDialogFragment} if user hasn't logged in.
     *
     * @return {@code true} if we need to show dialog, {@code false} otherwise.
     */
    public static boolean showLoginPromptDialogIfNeeded(FragmentActivity fragmentActivity, User user) {
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
        return new AlertDialog.Builder(getContext())
                .setMessage(R.string.dialog_message_login_prompt)
                .setPositiveButton(R.string.action_login, (dialog, which) ->
                        LoginActivity.startLoginActivityForResultMessage(getActivity()))
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "弹窗-登录提醒"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "弹窗-登录提醒"));
        super.onPause();
    }
}
