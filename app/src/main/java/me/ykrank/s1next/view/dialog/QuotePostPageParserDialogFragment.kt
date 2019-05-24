package me.ykrank.s1next.view.dialog

import android.os.Bundle
import com.github.ykrank.androidtools.extension.toast
import com.google.common.base.Optional
import com.google.common.base.Preconditions
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.ThreadLink
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.view.activity.PostListActivity
import java.util.regex.Pattern

/**
 * A [ProgressDialogFragment] parses post post page for thread.
 */
class QuotePostPageParserDialogFragment : ProgressDialogFragment<String>() {

    private var mShouldFinishActivity = true

    override fun onDestroy() {
        super.onDestroy()

        if (mShouldFinishActivity) {
            activity?.finish()
        }
    }

    override fun getSourceObservable(): Single<String> {
        val threadLink = arguments!!.getParcelable<ThreadLink>(
                ARG_THREAD_LINK)
        return mS1Service.getQuotePostResponseBody(threadLink.threadId,
                threadLink.quotePostId.get()).map { voidResponse -> voidResponse.raw().request().url().toString() }
    }

    override fun onNext(url: String) {
        val jumpPage = parseQuotePostPage(url)
        if (jumpPage.isPresent) {
            val threadLink = Preconditions.checkNotNull(arguments!!.getParcelable<ThreadLink>(
                    ARG_THREAD_LINK))
            val threadLinkWithJumpPage = ThreadLink.Builder(threadLink.threadId)
                    .jumpPage(jumpPage.get())
                    .quotePostId(threadLink.quotePostId.get())
                    .build()
            activity?.let {
                PostListActivity.start(it, threadLinkWithJumpPage)
            }
        } else {
            ThreadLinkInvalidPromptDialogFragment.newInstance(context,
                    getString(R.string.dialog_message_quote_not_found)).show(fragmentManager,
                    ThreadLinkInvalidPromptDialogFragment.TAG)
            mShouldFinishActivity = false
        }
    }

    override fun onError(throwable: Throwable) {
        val context = context ?: return
        val errorMsg = ErrorUtil.parse(context, throwable)
        if (isVisible) {
            ThreadLinkInvalidPromptDialogFragment.newInstance(context, errorMsg)
                    .show(fragmentManager, ThreadLinkInvalidPromptDialogFragment.TAG)
            mShouldFinishActivity = false
        } else {
            App.get().toast(errorMsg)
        }
    }

    override fun getProgressMessage(): CharSequence? {
        return getString(R.string.dialog_message_processing)
    }

    /**
     * Parses redirect link in order to get post post page.

     * @param url The redirect link.
     */
    private fun parseQuotePostPage(url: String): Optional<Int> {
        // example: http://bbs.saraba1st.com/2b/forum.php?mod=viewthread&tid=1074030&page=1#pid27217893
        val pattern = Pattern.compile("page=(\\d+)")
        val matcher = pattern.matcher(url)
        if (matcher.find()) {
            return Optional.of(Integer.parseInt(matcher.group(1)))
        }
        return Optional.absent<Int>()
    }

    companion object {

        val TAG = QuotePostPageParserDialogFragment::class.java.name

        private const val ARG_THREAD_LINK = "thread_link"

        fun newInstance(threadLink: ThreadLink): QuotePostPageParserDialogFragment {
            val fragment = QuotePostPageParserDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_THREAD_LINK, threadLink)
            fragment.arguments = bundle

            return fragment
        }
    }
}
