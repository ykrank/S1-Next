package me.ykrank.s1next.view.dialog;

import android.os.Bundle;

import io.reactivex.Observable;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Result;
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper;

/**
 * A {@link ProgressDialogFragment} posts a request to login to server.
 */
public final class AppLoginDialogFragment extends ProgressDialogFragment<AccountResultWrapper> {

    public static final String TAG = AppLoginDialogFragment.class.getName();

    private static final String ARG_USERNAME = "username";
    private static final String ARG_PASSWORD = "password";
    private static final String ARG_QUESTION_ID = "question_id";
    private static final String ARG_ANSWER = "answer";

    /**
     * For desktop is "login_succeed".
     * For mobile is "location_login_succeed_mobile".
     * "login_succeed" when already has logged in.
     */
    private static final String STATUS_AUTH_SUCCESS = "location_login_succeed_mobile";
    private static final String STATUS_AUTH_SUCCESS_ALREADY = "login_succeed";

    public static AppLoginDialogFragment newInstance(String username, String password, int questionId, String answer) {
        AppLoginDialogFragment fragment = new AppLoginDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_USERNAME, username);
        bundle.putString(ARG_PASSWORD, password);
        bundle.putInt(ARG_QUESTION_ID, questionId);
        bundle.putString(ARG_ANSWER, answer);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected Observable<AccountResultWrapper> getSourceObservable() {
        String username = getArguments().getString(ARG_USERNAME);
        String password = getArguments().getString(ARG_PASSWORD);
        int questionId = getArguments().getInt(ARG_QUESTION_ID);
        String answer = getArguments().getString(ARG_ANSWER);
        return mS1Service.login(username, password).map(resultWrapper -> {
            // the authenticity token is not fresh after login
            resultWrapper.getData().setAuthenticityToken(null);
            mUserValidator.validate(resultWrapper.getData());
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
