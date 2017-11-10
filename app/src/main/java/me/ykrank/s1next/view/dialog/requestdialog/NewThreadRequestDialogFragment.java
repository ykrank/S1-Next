package me.ykrank.s1next.view.dialog.requestdialog;

import android.os.Bundle;

import io.reactivex.Single;
import me.ykrank.s1next.BuildConfig;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Result;
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper;

/**
 * A dialog requests to reply to post.
 */
public final class NewThreadRequestDialogFragment extends BaseRequestDialogFragment<AccountResultWrapper> {

    public static final String TAG = NewThreadRequestDialogFragment.class.getName();

    private static final String ARG_FORUM_ID = "forum_id";
    private static final String ARG_TYPE_ID = "type_id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_CACHE_KEY = "cache_key";

    private static final String STATUS_NEW_THREAD_SUCCESS = "post_newthread_succeed";

    public static NewThreadRequestDialogFragment newInstance(int forumId, String typeId, String title,
                                                             String message, String cacheKey) {
        NewThreadRequestDialogFragment fragment = new NewThreadRequestDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_FORUM_ID, forumId);
        bundle.putString(ARG_TYPE_ID, typeId);
        bundle.putString(ARG_TITLE, title);
        bundle.putString(ARG_MESSAGE, message);
        bundle.putString(ARG_CACHE_KEY, cacheKey);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_message_reply);
    }

    @Override
    protected Single<AccountResultWrapper> getSourceObservable() {
        Bundle bundle = getArguments();
        int forumId = bundle.getInt(ARG_FORUM_ID);
        String title = bundle.getString(ARG_TITLE);
        String typeId = bundle.getString(ARG_TYPE_ID);
        String message = bundle.getString(ARG_MESSAGE);
        Integer saveAsDraft = BuildConfig.DEBUG ? 1 : null;

        return flatMappedWithAuthenticityToken(token ->
                mS1Service.newThread(forumId, token, System.currentTimeMillis(), typeId, title, message, 1, 1, saveAsDraft));
    }

    @Override
    protected void onNext(AccountResultWrapper data) {
        Result result = data.getResult();
        if (result.getStatus().equals(STATUS_NEW_THREAD_SUCCESS)) {
            onRequestSuccess(result.getMessage());
        } else {
            onRequestError(result.getMessage());
        }
    }
}
