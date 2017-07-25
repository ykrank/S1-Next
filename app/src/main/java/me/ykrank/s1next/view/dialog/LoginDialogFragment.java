package me.ykrank.s1next.view.dialog;

import android.os.Bundle;

import io.reactivex.Observable;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Result;
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper;

/**
 * A {@link ProgressDialogFragment} posts a request to login to server.
 */
public final class LoginDialogFragment extends ProgressDialogFragment<AccountResultWrapper> {

    public static final String TAG = LoginDialogFragment.class.getName();

    private static final String ARG_USERNAME = "username";
    private static final String ARG_PASSWORD = "password";

    /**
     * For desktop is "login_succeed".
     * For mobile is "location_login_succeed_mobile".
     * "login_succeed" when already has logged in.
     */
    private static final String STATUS_AUTH_SUCCESS = "location_login_succeed_mobile";
    private static final String STATUS_AUTH_SUCCESS_ALREADY = "login_succeed";

    public static LoginDialogFragment newInstance(String username, String password) {
        LoginDialogFragment fragment = new LoginDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_USERNAME, username);
        bundle.putString(ARG_PASSWORD, password);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected Observable<AccountResultWrapper> getSourceObservable() {
        String username = getArguments().getString(ARG_USERNAME);
        String password = getArguments().getString(ARG_PASSWORD);
        return getMS1Service().login(username, password).map(resultWrapper -> {
            // the authenticity token is not fresh after login
            resultWrapper.getData().setAuthenticityToken(null);
            getMUserValidator().validate(resultWrapper.getData());
            return resultWrapper;
        });
    }

    @Override
    protected void onNext(AccountResultWrapper data) {
        Result result = data.getResult();
        if (result.getStatus().equals(STATUS_AUTH_SUCCESS)
                || result.getStatus().equals(STATUS_AUTH_SUCCESS_ALREADY)) {
            showShortTextAndFinishCurrentActivity(result.getMessage());
        } else {
            showShortText(result.getMessage());
        }
    }

    @Override
    protected CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_message_login);
    }
}
