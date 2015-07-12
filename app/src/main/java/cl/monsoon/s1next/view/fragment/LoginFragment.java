package cl.monsoon.s1next.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Result;
import cl.monsoon.s1next.model.mapper.ResultWrapper;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpPostLoader;

/**
 * A login screen that offers login via username and password.
 */
public final class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();

    /**
     * For desktop is "login_succeed".
     * For mobile is "location_login_succeed_mobile".
     * "login_succeed" when already has logged in.
     */
    private static final String STATUS_AUTH_SUCCESS = "location_login_succeed_mobile";
    private static final String STATUS_AUTH_SUCCESS_ALREADY = "login_succeed";

    private EditText mUsernameView;
    private EditText mPasswordView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUsernameView = (EditText) view.findViewById(R.id.username);
        mPasswordView = (EditText) view.findViewById(R.id.password);

        // called when an ime action is performed
        // not working in some manufacturers
        mPasswordView.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == R.id.ime_login || i == EditorInfo.IME_NULL) {
                prepareLogin();
                return true;
            }
            return false;
        });

        Button loginView = (Button) view.findViewById(R.id.login);
        loginView.setOnClickListener(v -> prepareLogin());
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
                IntentUtil.startViewIntentExcludeOurApp(getActivity(),
                        Uri.parse(Api.URL_BROWSER_REGISTER));

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
            focusView = mUsernameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(error);
            if (focusView == null) {
                focusView = mPasswordView;
            }
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // start to log in
            LoginLoaderDialogFragment.newInstance(username, password).show(
                    getChildFragmentManager(), LoginLoaderDialogFragment.TAG);
        }
    }

    public static class LoginLoaderDialogFragment extends LoaderDialogFragment<ResultWrapper> {

        private static final String TAG = LoginLoaderDialogFragment.class.getSimpleName();

        private static final String ARG_USERNAME = "username";
        private static final String ARG_PASSWORD = "password";

        public static LoginLoaderDialogFragment newInstance(String username, String password) {
            LoginLoaderDialogFragment fragment = new LoginLoaderDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_USERNAME, username);
            bundle.putString(ARG_PASSWORD, password);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        protected CharSequence getProgressMessage() {
            return getText(R.string.dialog_progress_message_login);
        }

        @Override
        public Loader<AsyncResult<ResultWrapper>> onCreateLoader(@LoaderId int id, Bundle args) {
            return new HttpPostLoader<>(getActivity(), Api.URL_LOGIN, ResultWrapper.class,
                    Api.getLoginPostBuilder(getArguments().getString(ARG_USERNAME),
                            getArguments().getString(ARG_PASSWORD)));
        }

        @Override
        public void onLoadFinished(Loader<AsyncResult<ResultWrapper>> loader, AsyncResult<ResultWrapper> asyncResult) {
            if (asyncResult.exception != null) {
                ToastUtil.showByResId(asyncResult.getExceptionStringRes(), Toast.LENGTH_SHORT);
            } else {
                ResultWrapper wrapper = asyncResult.data;
                Result result = wrapper.getResult();

                ToastUtil.showByText(result.getMessage(), Toast.LENGTH_LONG);

                if (result.getStatus().equals(STATUS_AUTH_SUCCESS)
                        || result.getStatus().equals(STATUS_AUTH_SUCCESS_ALREADY)) {
                    // this authenticity token is not fresh
                    // we need to abandon this token
                    User.setAuthenticityToken(null);

                    new Handler().post(() -> getActivity().onBackPressed());
                }
            }

            new Handler().post(this::dismiss);
        }
    }
}
