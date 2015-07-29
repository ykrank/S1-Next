package cl.monsoon.s1next.view.fragment;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.S1Service;
import cl.monsoon.s1next.data.api.model.Result;
import cl.monsoon.s1next.data.api.model.wrapper.ResultWrapper;
import cl.monsoon.s1next.databinding.FragmentLoginBinding;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.view.dialog.ProgressDialogFragment;
import rx.Observable;

/**
 * A login screen that offers login via username and password.
 */
public final class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getName();

    /**
     * For desktop is "login_succeed".
     * For mobile is "location_login_succeed_mobile".
     * "login_succeed" when already has logged in.
     */
    private static final String STATUS_AUTH_SUCCESS = "location_login_succeed_mobile";
    private static final String STATUS_AUTH_SUCCESS_ALREADY = "login_succeed";

    private EditText mUsernameView;
    private EditText mPasswordView;
    private Button mLoginView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentLoginBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,
                container, false);
        mUsernameView = binding.username;
        mPasswordView = binding.password;
        mLoginView = binding.login;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // called when an ime action is performed
        // not working in some manufacturers
        mPasswordView.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == R.id.ime_login) {
                prepareLogin();
                return true;
            }
            return false;
        });

        mLoginView.setOnClickListener(v -> prepareLogin());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_login, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_account_new:
                IntentUtil.startViewIntentExcludeOurApp(getActivity(), Uri.parse(
                        Api.URL_BROWSER_REGISTER));

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareLogin() {
        // reset errors
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        CharSequence error = getText(R.string.error_field_required);
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(error);
            cancel = true;
            focusView = mUsernameView;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(error);
            cancel = true;
            if (focusView == null) {
                focusView = mPasswordView;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // start to log in
            LoginDialogFragment.newInstance(username, password).show(getFragmentManager(),
                    LoginDialogFragment.TAG);
        }
    }

    public static class LoginDialogFragment extends ProgressDialogFragment<ResultWrapper> {

        private static final String TAG = LoginDialogFragment.class.getName();

        private static final String ARG_USERNAME = "username";
        private static final String ARG_PASSWORD = "password";

        @Inject
        S1Service mS1Service;

        @Inject
        User mUser;

        private static LoginDialogFragment newInstance(String username, String password) {
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
            return mS1Service.login(getArguments().getString(ARG_USERNAME), getArguments().getString(
                    ARG_PASSWORD));
        }

        @Override
        protected void onNext(ResultWrapper data) {
            Result result = data.getResult();
            ToastUtil.showByText(result.getMessage(), Toast.LENGTH_LONG);

            if (result.getStatus().equals(STATUS_AUTH_SUCCESS)
                    || result.getStatus().equals(STATUS_AUTH_SUCCESS_ALREADY)) {
                // this authenticity token is not fresh
                // we need to abandon this token
                mUser.setAuthenticityToken(null);

                getActivity().finish();
            }
        }

        @Override
        protected CharSequence getProgressMessage() {
            return getText(R.string.dialog_progress_message_login);
        }
    }
}
