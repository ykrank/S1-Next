package me.ykrank.s1next.view.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
import me.ykrank.s1next.BuildConfig;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * A dialog requests to reply to post.
 */
public final class EditPostRequestDialogFragment extends ProgressDialogFragment<String> {

    public static final String TAG = EditPostRequestDialogFragment.class.getName();

    private static final String ARG_THREAD = "thread";
    private static final String ARG_POST = "post";
    private static final String ARG_TYPE_ID = "type_id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    public static EditPostRequestDialogFragment newInstance(@NonNull Thread thread, @NonNull Post post, String typeId, String title,
                                                            String message) {
        EditPostRequestDialogFragment fragment = new EditPostRequestDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_THREAD, thread);
        bundle.putParcelable(ARG_POST, post);
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
    protected Observable<String> getSourceObservable() {
        Bundle bundle = getArguments();
        Thread mThread = bundle.getParcelable(ARG_THREAD);
        Post mPost = bundle.getParcelable(ARG_POST);
        String title = bundle.getString(ARG_TITLE);
        String typeId = bundle.getString(ARG_TYPE_ID);
        String message = bundle.getString(ARG_MESSAGE);

        if (mPost == null || mThread == null) {
            return Observable.error(new NullPointerException());
        }

        Integer saveAsDraft = BuildConfig.DEBUG && mPost.isFirst() ? 1 : null;
        return flatMappedWithAuthenticityToken(token ->
                mS1Service.editPost(mThread.getFid(), mThread.getId(), mPost.getId(), token, System.currentTimeMillis(), typeId, title, message, 1, 1, saveAsDraft));
    }

    @Override
    protected void onNext(String data) {
        if (data.contains("succeedhandle_")) {
            showShortTextAndFinishCurrentActivity(getString(R.string.edit_post_succeed));
        } else {
            showShortText(data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "弹窗-编辑帖子进度条"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "弹窗-编辑帖子进度条"));
        super.onPause();
    }
}
