package cl.monsoon.s1next.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
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
import android.widget.EditText;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Authority;
import cl.monsoon.s1next.model.mapper.AuthorityWrapper;
import cl.monsoon.s1next.util.ToastHelper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpPostLoader;

/**
 * A login screen that offers login via username/password.
 */
public final class LoginFragment extends Fragment implements LoaderManager.LoaderCallbacks<AsyncResult<AuthorityWrapper>> {

    public static final String TAG = "login_fragment";

    /**
     * The serialization (saved instance state) Bundle key representing
     * whether is logging when configuration changed.
     */
    private static final String STATE_LOGGING = "logging";

    /**
     * For desktop is "login_succeed".
     * For mobile is "location_login_succeed_mobile".
     * "login_succeed" when already has logged.
     */
    private static final String STATUS_AUTH_SUCCESS = "location_login_succeed_mobile";
    private static final String STATUS_AUTH_SUCCESS_ALREAY = "login_succeed";

    private static final int ID_LOADER = 0;
    private Loader mLoader;

    private EditText mUsernameView;
    private EditText mPasswordView;
    private ProgressDialog mProgressDialog;

    private Boolean mLogging = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mUsernameView = (EditText) view.findViewById(R.id.drawer_username);
        mPasswordView = (EditText) view.findViewById(R.id.password);

        // called when an ime action is performed
        // not working in some manufacturers
        mPasswordView.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == R.id.ime_login || i == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        view.findViewById(R.id.login).setOnClickListener(v -> attemptLogin());

        if (savedInstanceState != null) {
            mLogging = savedInstanceState.getBoolean(STATE_LOGGING);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mLogging) {
            showProgressDialog();
            mLoader = getLoaderManager().initLoader(ID_LOADER, null, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        dismissProgressDialog();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_login, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_account_add:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Api.URL_REGISTER));

                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_LOGGING, mLogging);
    }

    private void attemptLogin() {
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
            // Show a progress dialog, and kick off a background task to
            // perform the user login attempt.
            showProgressDialog();

            // start to log in
            mLoader = getLoaderManager().getLoader(ID_LOADER);
            if (mLoader == null) {
                mLoader = getLoaderManager().initLoader(ID_LOADER, null, this);
            } else {
                try {
                    ((HttpPostLoader) mLoader)
                            .setRequestBody(
                                    Api.getLoginBuilder(
                                            mUsernameView.getText(),
                                            mPasswordView.getText()));
                } catch (ClassCastException e) {
                    throw new IllegalStateException(mLoader + " must extend HttpPostLoader.");
                }

                mLoader.onContentChanged();
            }
            mLogging = true;
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getText(R.string.dialog_progress_title_login));
            mProgressDialog.setOnCancelListener(dialog -> {
                // see HttpGetLoader#cancelLoad()
                //noinspection RedundantCast
                ((HttpPostLoader) mLoader).cancelLoad();
                mLogging = false;
            });
        }

        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public Loader<AsyncResult<AuthorityWrapper>> onCreateLoader(int id, Bundle args) {
        return
                new HttpPostLoader<>(
                        getActivity(),
                        Api.URL_LOGIN,
                        AuthorityWrapper.class,
                        Api.getLoginBuilder(mUsernameView.getText(), mPasswordView.getText()));
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<AuthorityWrapper>> loader, AsyncResult<AuthorityWrapper> asyncResult) {
        mLogging = false;
        dismissProgressDialog();

        if (asyncResult.exception != null) {
            AsyncResult.handleException(asyncResult.exception);
        } else {
            AuthorityWrapper wrapper = asyncResult.data;
            Authority authority = wrapper.getAuthority();

            ToastHelper.showByText(authority.getMessage());

            if (authority.getStatus().equals(STATUS_AUTH_SUCCESS)
                    || authority.getStatus().equals(STATUS_AUTH_SUCCESS_ALREAY)) {
                getActivity().onBackPressed();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<AsyncResult<AuthorityWrapper>> loader) {

    }
}
