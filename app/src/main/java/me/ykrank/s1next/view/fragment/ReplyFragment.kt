package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.util.AppDeviceUtil
import me.ykrank.s1next.view.dialog.requestdialog.ReplyRequestDialogFragment
import me.ykrank.s1next.view.event.RequestDialogSuccessEvent

/**
 * A Fragment shows [EditText] to let the user enter reply.
 */
class ReplyFragment : BasePostFragment() {
    override var cacheKey: String? = null
        private set

    private var mThreadId: String? = null
    private var mQuotePostId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mThreadId = arguments.getString(ARG_THREAD_ID)
        mQuotePostId = arguments.getString(ARG_QUOTE_POST_ID)
        cacheKey = String.format(CACHE_KEY_PREFIX, mThreadId, mQuotePostId)
        L.leaveMsg("ReplyFragment##mThreadId:$mThreadId,mQuotePostId$mQuotePostId")
    }

    override fun onMenuSendClick(): Boolean {
        val stringBuilder = StringBuilder(mReplyView.text)
        if (mGeneralPreferencesManager.isSignatureEnabled) {
            stringBuilder.append("\n\n").append(AppDeviceUtil.getPostSignature(context))
        }

        ReplyRequestDialogFragment.newInstance(mThreadId, mQuotePostId,
                stringBuilder.toString()).show(fragmentManager,
                ReplyRequestDialogFragment.TAG)

        return true
    }

    override fun isRequestDialogAccept(event: RequestDialogSuccessEvent): Boolean {
        return event.dialogFragment is ReplyRequestDialogFragment
    }

    companion object {

        val TAG: String = ReplyFragment::class.java.name

        private val ARG_THREAD_ID = "thread_id"
        private val ARG_QUOTE_POST_ID = "quote_post_id"

        private val CACHE_KEY_PREFIX = "NewReply_%s_%s"

        fun newInstance(threadId: String, quotePostId: String?): ReplyFragment {
            val fragment = ReplyFragment()
            val bundle = Bundle()
            bundle.putString(ARG_THREAD_ID, threadId)
            bundle.putString(ARG_QUOTE_POST_ID, quotePostId)
            fragment.arguments = bundle

            return fragment
        }
    }
}
