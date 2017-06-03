package me.ykrank.s1next.view.dialog;

import android.os.Bundle;

import javax.inject.Inject;

import io.reactivex.Observable;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.app.AppService;
import me.ykrank.s1next.data.api.app.model.AppDataWrapper;
import me.ykrank.s1next.data.api.app.model.AppLoginResult;
import me.ykrank.s1next.data.event.AppLoginEvent;
import me.ykrank.s1next.widget.EventBus;

/**
 * A {@link ProgressDialogFragment} posts a request to login to server.
 */
public final class AppLoginDialogFragment extends ProgressDialogFragment<AppDataWrapper<AppLoginResult>> {

    public static final String TAG = AppLoginDialogFragment.class.getName();

    @Inject
    AppService mAppService;
    @Inject
    EventBus eventBus;

    private static final String ARG_USERNAME = "username";
    private static final String ARG_PASSWORD = "password";
    private static final String ARG_QUESTION_ID = "question_id";
    private static final String ARG_ANSWER = "answer";

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
    public void onCreate(Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Observable<AppDataWrapper<AppLoginResult>> getSourceObservable() {
        String username = getArguments().getString(ARG_USERNAME);
        String password = getArguments().getString(ARG_PASSWORD);
        int questionId = getArguments().getInt(ARG_QUESTION_ID);
        String answer = getArguments().getString(ARG_ANSWER);
        return mAppService.login(username, password, questionId, answer);
    }

    @Override
    protected void onNext(AppDataWrapper<AppLoginResult> data) {
        if (data.isSuccess()) {
            if (mUserValidator.validateAppLoginInfo(data.getData())) {
                eventBus.post(new AppLoginEvent());
                showShortTextAndFinishCurrentActivity(data.getMessage());
            } else {
                showShortText(getString(R.string.app_login_info_error));
            }
        } else {
            showShortText(data.getMessage());
        }
    }

    @Override
    protected CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_message_login);
    }
}
