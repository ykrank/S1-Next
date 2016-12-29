package me.ykrank.s1next.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.api.model.Quote;
import me.ykrank.s1next.data.api.model.Result;
import me.ykrank.s1next.data.api.model.wrapper.ResultWrapper;
import me.ykrank.s1next.widget.track.event.NewReplyTrackEvent;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;
import rx.Observable;

/**
 * A dialog requests to reply to post.
 */
public final class ReplyRequestDialogFragment extends ProgressDialogFragment<ResultWrapper> {

    public static final String TAG = ReplyRequestDialogFragment.class.getName();

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_REPLY = "reply";
    private static final String ARG_QUOTE_POST_ID = "quote_post_id";

    private static final String STATUS_REPLY_SUCCESS = "post_reply_succeed";

    public static ReplyRequestDialogFragment newInstance(String threadId, @Nullable String quotePostId, String reply) {
        App.get().getTrackAgent().post(new NewReplyTrackEvent(threadId, quotePostId));
        
        ReplyRequestDialogFragment fragment = new ReplyRequestDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_THREAD_ID, threadId);
        bundle.putString(ARG_QUOTE_POST_ID, quotePostId);
        bundle.putString(ARG_REPLY, reply);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_message_reply);
    }

    @Override
    protected Observable<ResultWrapper> getSourceObservable() {
        String threadId = getArguments().getString(ARG_THREAD_ID);
        String quotePostId = getArguments().getString(ARG_QUOTE_POST_ID);
        String reply = getArguments().getString(ARG_REPLY);

        if (TextUtils.isEmpty(quotePostId)) {
            return flatMappedWithAuthenticityToken(s -> mS1Service.reply(s, threadId, reply));
        } else {
            return mS1Service.getQuoteInfo(threadId, quotePostId).flatMap(s -> {
                Quote quote = Quote.fromXmlString(s);
                return flatMappedWithAuthenticityToken(token ->
                        mS1Service.replyQuote(token, threadId, reply, quote.getEncodedUserId(),
                                quote.getQuoteMessage(), StringUtils.abbreviate(reply,
                                        Api.REPLY_NOTIFICATION_MAX_LENGTH)));
            });
        }
    }

    @Override
    protected void onNext(ResultWrapper data) {
        Result result = data.getResult();
        if (result.getStatus().equals(STATUS_REPLY_SUCCESS)) {
            showShortTextAndFinishCurrentActivity(result.getMessage());
        } else {
            showShortText(result.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "弹窗-回复进度条-"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "弹窗-回复进度条-"));
        super.onPause();
    }
}
