package me.ykrank.s1next.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;

import io.reactivex.Observable;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.api.model.Quote;
import me.ykrank.s1next.data.api.model.Result;
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper;
import me.ykrank.s1next.widget.EditorDiskCache;
import me.ykrank.s1next.widget.track.event.NewReplyTrackEvent;

/**
 * A dialog requests to reply to post.
 */
public final class ReplyRequestDialogFragment extends ProgressDialogFragment<AccountResultWrapper> {

    public static final String TAG = ReplyRequestDialogFragment.class.getName();

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_REPLY = "reply";
    private static final String ARG_QUOTE_POST_ID = "quote_post_id";
    private static final String ARG_CACHE_KEY = "cache_key";

    private static final String STATUS_REPLY_SUCCESS = "post_reply_succeed";

    private String cacheKey;

    public static ReplyRequestDialogFragment newInstance(String threadId, @Nullable String quotePostId,
                                                         String reply, String cacheKey) {
        App.get().getTrackAgent().post(new NewReplyTrackEvent(threadId, quotePostId));
        
        ReplyRequestDialogFragment fragment = new ReplyRequestDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_THREAD_ID, threadId);
        bundle.putString(ARG_QUOTE_POST_ID, quotePostId);
        bundle.putString(ARG_REPLY, reply);
        bundle.putString(ARG_CACHE_KEY, cacheKey);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_message_reply);
    }

    @Override
    protected Observable<AccountResultWrapper> getSourceObservable() {
        String threadId = getArguments().getString(ARG_THREAD_ID);
        String quotePostId = getArguments().getString(ARG_QUOTE_POST_ID);
        String reply = getArguments().getString(ARG_REPLY);
        cacheKey = getArguments().getString(ARG_CACHE_KEY);

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
    protected void onNext(AccountResultWrapper data) {
        Result result = data.getResult();
        if (result.getStatus().equals(STATUS_REPLY_SUCCESS)) {
            EditorDiskCache.remove(cacheKey);
            showShortTextAndFinishCurrentActivity(result.getMessage());
        } else {
            showShortText(result.getMessage());
        }
    }
}
