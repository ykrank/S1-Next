package cl.monsoon.s1next.fragment;

import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
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

import com.squareup.okhttp.RequestBody;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Result;
import cl.monsoon.s1next.model.mapper.ResultWrapper;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpPostLoader;

/**
 * A login screen that offers login via username/password.
 */
public final class LoginFragment extends LoaderFragment<ResultWrapper> {

    public static final String TAG = "login_fragment";

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
        Config.updateTextSize(mUsernameView);
        Config.updateTextSize(mPasswordView);

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
        Config.updateTextSize(loginView);
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
            case R.id.menu_account_add:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Api.URL_BROWSER_REGISTER));

                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareLogin() {
        // reset errors
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        CharSequence username = mUsernameView.getText();
        CharSequence password = mPasswordView.getText();

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
            showProgressDialog();
            // start to log in
            startLoader(ID_LOADER_LOGIN);
        }
    }

    @Override
    public CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_title_login);
    }

    @Override
    RequestBody getRequestBody(int loaderId) {
        if (loaderId == ID_LOADER_LOGIN) {
            return Api.getLoginPostBuilder(mUsernameView.getText(), mPasswordView.getText());
        }

        return super.getRequestBody(loaderId);
    }

    @Override
    public Loader<AsyncResult<ResultWrapper>> onCreateLoader(int id, Bundle args) {
        return
                new HttpPostLoader<>(
                        getActivity(),
                        Api.URL_LOGIN,
                        ResultWrapper.class,
                        getRequestBody(id));
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<ResultWrapper>> loader, AsyncResult<ResultWrapper> asyncResult) {
        dismissProgressDialog();

        if (asyncResult.exception != null) {
            AsyncResult.handleException(asyncResult.exception);
        } else {
            ResultWrapper wrapper = asyncResult.data;
            Result result = wrapper.getResult();

            ToastUtil.showByText(result.getValue(), Toast.LENGTH_LONG);

            if (result.getStatus().equals(STATUS_AUTH_SUCCESS)
                    || result.getStatus().equals(STATUS_AUTH_SUCCESS_ALREADY)) {
                // this authenticity token is not fresh
                // we need abandon this token
                User.setAuthenticityToken(null);

                getActivity().onBackPressed();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<AsyncResult<ResultWrapper>> loader) {

    }
}
