package me.ykrank.s1next.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;

import com.github.ykrank.androidtools.util.WebViewUtils;

import java.net.CookieManager;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.viewmodel.UserViewModel;

/**
 * A dialog shows logout prompt.
 * Logs out if user clicks the logout button.
 */
public final class LogoutDialogFragment extends BaseDialogFragment {

    private static final String TAG = LogoutDialogFragment.class.getName();

    @Inject
    CookieManager mCookieManager;

    @Inject
    UserViewModel mUser;

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
        App.Companion.getAppComponent().inject(this);
        return new AlertDialog.Builder(getContext())
                .setMessage(R.string.dialog_message_log_out)
                .setPositiveButton(R.string.dialog_button_text_log_out, (dialog, which) -> logout())
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    /**
     * Clears user's cookies and current user's info.
     */
    private void logout() {
        mCookieManager.getCookieStore().removeAll();
        WebViewUtils.INSTANCE.clearWebViewCookies(App.Companion.get());
        mUser.getUser().setAppSecureToken(null);
        mUser.getUser().setLogged(false);
    }
}
