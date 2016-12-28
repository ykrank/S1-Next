package me.ykrank.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import java.net.CookieManager;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.util.WebViewUtils;
import me.ykrank.s1next.widget.track.event.PageEndEvent;
import me.ykrank.s1next.widget.track.event.PageStartEvent;

/**
 * A dialog shows logout prompt.
 * Logs out if user clicks the logout button.
 */
public final class LogoutDialogFragment extends BaseDialogFragment {

    private static final String TAG = LogoutDialogFragment.class.getName();

    @Inject
    CookieManager mCookieManager;

    @Inject
    User mUser;

    /**
     * Show {@link LogoutDialogFragment} if user has logged in.
     *
     * @return {@code true} if we need to show dialog, {@code false} otherwise.
     */
    public static boolean showLogoutDialogIfNeeded(FragmentActivity fragmentActivity, User user) {
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
        App.getAppComponent(getContext()).inject(this);
        return new AlertDialog.Builder(getContext())
                .setMessage(R.string.dialog_message_log_out)
                .setPositiveButton(R.string.dialog_button_text_log_out, (dialog, which) -> logout())
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent("弹窗-登录退出提醒-" + TAG));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent("弹窗-登录退出提醒-" + TAG));
        super.onPause();
    }

    /**
     * Clears user's cookies and current user's info.
     */
    private void logout() {
        mCookieManager.getCookieStore().removeAll();
        WebViewUtils.clearCookies(App.get());
        mUser.setLogged(false);
    }
}
