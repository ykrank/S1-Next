package me.ykrank.s1next.view.dialog.requestdialog

import android.os.Bundle
import android.text.TextUtils
import com.github.ykrank.androidtools.util.StringUtils
import io.reactivex.Single
import me.ykrank.s1next.App.Companion.get
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.model.Quote
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper
import me.ykrank.s1next.widget.track.event.NewReplyTrackEvent

/**
 * A dialog requests to reply to post.
 */
class ReplyRequestDialogFragment : BaseRequestDialogFragment<AccountResultWrapper>() {
    override fun getProgressMessage(): CharSequence? {
        return getText(R.string.dialog_progress_message_reply)
    }

    override fun getSourceObservable(): Single<AccountResultWrapper> {
        val threadId = requireArguments().getString(ARG_THREAD_ID)
        val quotePostId = requireArguments().getString(ARG_QUOTE_POST_ID)
        val reply = requireArguments().getString(ARG_REPLY)
        return if (TextUtils.isEmpty(quotePostId)) {
            flatMappedWithAuthenticityToken { s: String? -> mS1Service.reply(s, threadId, reply) }
        } else {
            mS1Service.getQuoteInfo(threadId, quotePostId).flatMap { s: String? ->
                val quote = Quote.fromXmlString(s)
                    ?: throw IllegalStateException("Cannot get the post information.")
                flatMappedWithAuthenticityToken { token: String? ->
                    mS1Service.replyQuote(
                        token, threadId, reply, quote.encodedUserId,
                        quote.quoteMessage, StringUtils.abbreviate(
                            reply,
                            Api.REPLY_NOTIFICATION_MAX_LENGTH
                        )
                    )
                }
            }
        }
    }

    override fun onNext(data: AccountResultWrapper) {
        val result = data.result
        if (result.defaultSuccess) {
            onRequestSuccess(result.message)
        } else {
            onRequestError(result.message)
        }
    }

    companion object {
        val TAG: String = ReplyRequestDialogFragment::class.java.getName()
        private const val ARG_THREAD_ID = "thread_id"
        private const val ARG_REPLY = "reply"
        private const val ARG_QUOTE_POST_ID = "quote_post_id"
        private const val STATUS_REPLY_SUCCESS = "post_reply_succeed"
        fun newInstance(
            threadId: String?, quotePostId: String?,
            reply: String?
        ): ReplyRequestDialogFragment {
            get().trackAgent.post(NewReplyTrackEvent(threadId, quotePostId))
            val fragment = ReplyRequestDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARG_THREAD_ID, threadId)
            bundle.putString(ARG_QUOTE_POST_ID, quotePostId)
            bundle.putString(ARG_REPLY, reply)
            fragment.setArguments(bundle)
            return fragment
        }
    }
}
