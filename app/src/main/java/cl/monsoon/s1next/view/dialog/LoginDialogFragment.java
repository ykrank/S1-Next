package cl.monsoon.s1next.view.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.S1Service;
import cl.monsoon.s1next.data.api.model.Result;
import cl.monsoon.s1next.data.api.model.wrapper.ResultWrapper;
import cl.monsoon.s1next.util.ToastUtil;
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

    @Inject
    S1Service mS1Service;

    @Inject
    User mUser;

    public static LoginDialogFragment newInstance(String username, String password) {
        LoginDialogFragment fragment = new LoginDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_USERNAME, username);
        bundle.putString(ARG_PASSWORD, password);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        App.getAppComponent(activity).inject(this);
    }

    @Override
    protected Observable<ResultWrapper> getSourceObservable() {
        Bundle bundle = getArguments();
        String username = bundle.getString(ARG_USERNAME);
        String password = bundle.getString(ARG_PASSWORD);
        // the authenticity token is not fresh after login
        // so we need to refresh authenticity token
        return Observable.zip(mS1Service.login(username, password),
                mS1Service.refreshAuthenticityToken(), (resultWrapper, aVoid) -> resultWrapper);
    }

    @Override
    protected void onNext(ResultWrapper data) {
        Result result = data.getResult();
        ToastUtil.showByText(result.getMessage(), Toast.LENGTH_LONG);

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
