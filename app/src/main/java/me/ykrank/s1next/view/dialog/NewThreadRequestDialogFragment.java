package me.ykrank.s1next.view.dialog;

import android.os.Bundle;

import me.ykrank.s1next.BuildConfig;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Result;
import me.ykrank.s1next.data.api.model.wrapper.ResultWrapper;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;
import rx.Observable;

/**
 * A dialog requests to reply to post.
 */
public final class NewThreadRequestDialogFragment extends ProgressDialogFragment<ResultWrapper> {

    public static final String TAG = NewThreadRequestDialogFragment.class.getName();

    private static final String ARG_FORUM_ID = "forum_id";
    private static final String ARG_TYPE_ID = "type_id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    private static final String STATUS_NEW_THREAD_SUCCESS = "post_newthread_succeed";

    public static NewThreadRequestDialogFragment newInstance(int forumId, String typeId, String title, String message) {
        NewThreadRequestDialogFragment fragment = new NewThreadRequestDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_FORUM_ID, forumId);
        bundle.putString(ARG_TYPE_ID, typeId);
        bundle.putString(ARG_TITLE, title);
        bundle.putString(ARG_MESSAGE, message);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_message_reply);
    }

    @Override
    protected Observable<ResultWrapper> getSourceObservable() {
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
    protected void onNext(ResultWrapper data) {
        Result result = data.getResult();
        if (result.getStatus().equals(STATUS_NEW_THREAD_SUCCESS)) {
            showShortTextAndFinishCurrentActivity(result.getMessage());
        } else {
            showShortText(result.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "弹窗-新帖发布进度条"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "弹窗-新帖发布进度条"));
        super.onPause();
    }
}
