package me.ykrank.s1next.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.databinding.FragmentAppLoginBinding;
import me.ykrank.s1next.util.IntentUtil;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.ViewUtil;
import me.ykrank.s1next.view.dialog.AppLoginDialogFragment;

/**
 * A Fragment offers login via username and password.
 */
public final class AppLoginFragment extends BaseFragment {

    public static final String TAG = AppLoginFragment.class.getName();

    private EditText mUsernameView;
    private EditText mPasswordView;
    private Button mLoginButton;
    private FragmentAppLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAppLoginBinding.inflate(inflater, container, false);
        mUsernameView = binding.username;
        mPasswordView = binding.password;
        mLoginButton = binding.login;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        L.leaveMsg("AppLoginFragment");

        ViewUtil.consumeRunnableWhenImeActionPerformed(mPasswordView, this::prepareLogin);
        mLoginButton.setOnClickListener(v -> prepareLogin());
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
                IntentUtil.startViewIntentExcludeOurApp(getContext(), Uri.parse(
                        Api.URL_BROWSER_REGISTER));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void prepareLogin() {
        // reset errors
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        int questionId = binding.questionSpinner.getSelectedItemPosition();
        String answer = binding.answer.getText().toString();

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
            AppLoginDialogFragment.Companion.newInstance(username, password, questionId, answer).show(getFragmentManager(),
                    AppLoginDialogFragment.Companion.getTAG());
        }
    }
}
