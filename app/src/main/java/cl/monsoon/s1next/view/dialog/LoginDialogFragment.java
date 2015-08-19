package cl.monsoon.s1next.view.dialog;

import android.os.Bundle;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Result;
import cl.monsoon.s1next.data.api.model.wrapper.ResultWrapper;
import rx.Observable;

/**
 * A {@link ProgressDialogFragment} posts a request to login to server.
 */
public final class LoginDialogFragment extends ProgressDialogFragment<ResultWrapper> {

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
    protected Observable<ResultWrapper> getSourceObservable() {
        String username = getArguments().getString(ARG_USERNAME);
        String password = getArguments().getString(ARG_PASSWORD);
        return mS1Service.login(username, password).map(resultWrapper -> {
            // the authenticity token is not fresh after login
            resultWrapper.getAccount().setAuthenticityToken(null);
            mUserValidator.validate(resultWrapper.getAccount());
            return resultWrapper;
        });
    }

    @Override
    protected void onNext(ResultWrapper data) {
        Result result = data.getResult();
        showApplicationToastForResultMessage(data.getResult());

        if (result.getStatus().equals(STATUS_AUTH_SUCCESS)
                || result.getStatus().equals(STATUS_AUTH_SUCCESS_ALREADY)) {
            getActivity().finish();
        }
    }

    @Override
    protected CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_message_login);
    }
}
