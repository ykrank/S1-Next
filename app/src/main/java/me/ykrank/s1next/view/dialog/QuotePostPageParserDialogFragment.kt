package me.ykrank.s1next.view.dialog

import android.os.Build
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.github.ykrank.androidtools.extension.toast
import io.reactivex.Single
import kotlinx.coroutines.launch
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.link.ThreadLink
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.view.page.post.postlist.PostListActivity

/**
 * A [ProgressDialogFragment] parses post post page for thread.
 */
class QuotePostPageParserDialogFragment : ProgressDialogFragment<String>() {

    private var threadLink: ThreadLink? = null
    private var mShouldFinishActivity = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            threadLink = requireArguments().getParcelable(ARG_THREAD_LINK, ThreadLink::class.java)
        } else {
            threadLink = requireArguments().getParcelable(ARG_THREAD_LINK)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mShouldFinishActivity) {
            activity?.finish()
        }
    }

    override fun getSourceObservable(): Single<String> {
        val threadLink = this.threadLink ?: return Single.error(IllegalStateException())
        return mS1Service.getQuotePostResponseBody(threadLink.threadId,
            threadLink.quotePostId
        ).map { voidResponse -> voidResponse.raw().request.url.toString() }
    }

    override fun onNext(data: String) {
        val threadLink = this.threadLink ?: return
        val jumpPage = parseQuotePostPage(data)
        if (jumpPage != null) {
            val threadLinkWithJumpPage = ThreadLink.Builder(threadLink.threadId)
                    .jumpPage(jumpPage)
                .quotePostId(threadLink.quotePostId)
                    .build()
            activity?.let {
                PostListActivity.start(it, threadLinkWithJumpPage)
            }
        } else {
            ThreadLinkInvalidPromptDialogFragment.newInstance(context,
                getString(R.string.dialog_message_quote_not_found)
            ).show(childFragmentManager, ThreadLinkInvalidPromptDialogFragment.TAG)
            mShouldFinishActivity = false
        }
    }

    override fun onError(throwable: Throwable) {
        val context = context ?: return
        lifecycleScope.launch {
            val errorMsg = ErrorUtil.parse(context, throwable)
            if (isVisible) {
                ThreadLinkInvalidPromptDialogFragment.newInstance(context, errorMsg)
                    .show(childFragmentManager, ThreadLinkInvalidPromptDialogFragment.TAG)
                mShouldFinishActivity = false
            } else {
                App.get().toast(errorMsg)
            }
        }
    }

    override fun getProgressMessage(): CharSequence? {
        return getString(R.string.dialog_message_processing)
    }

    /**
     * Parses redirect link in order to get post post page.

     * @param url The redirect link.
     */
    private fun parseQuotePostPage(url: String): Int? {
        // example: http://bbs.saraba1st.com/2b/forum.php?mod=viewthread&tid=1074030&page=1#pid27217893
        val page = "page=(\\d+)".toRegex().find(url)?.groupValues?.getOrNull(1)
        return page?.toIntOrNull()
    }

    companion object {

        val TAG = QuotePostPageParserDialogFragment::class.java.simpleName

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
