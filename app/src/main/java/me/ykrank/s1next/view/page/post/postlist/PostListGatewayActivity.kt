package me.ykrank.s1next.view.page.post.postlist

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.link.ThreadLink
import me.ykrank.s1next.data.pref.ThemeManager
import me.ykrank.s1next.view.dialog.QuotePostPageParserDialogFragment
import me.ykrank.s1next.view.dialog.ThreadLinkInvalidPromptDialogFragment
import javax.inject.Inject

/**
 * An Activity to detect whether the thread link (URI) from Intent is valid.
 * Also show prompt if the thread corresponding to url do not exist.
 *
 *
 * This Activity is only used for Intent filter.
 */
class PostListGatewayActivity : FragmentActivity() {

    @Inject
    lateinit var mThemeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // default theme for this Activity is light theme
        App.appComponent.inject(this)
        if (mThemeManager.isDarkTheme) {
            setTheme(ThemeManager.TRANSLUCENT_THEME_DARK)
        }

        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            var threadLink = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.extras?.getParcelable(ARG_THREAD_LINK, ThreadLink::class.java)
            } else {
                intent.extras?.getParcelable(ARG_THREAD_LINK)
            }
            if (threadLink == null) {
                val uri = intent.data
                threadLink = ThreadLink.parse(uri.toString())
            }
            if (threadLink != null) {
                if (!threadLink.quotePostId.isNullOrEmpty()) {
                    QuotePostPageParserDialogFragment.newInstance(threadLink).show(
                            supportFragmentManager, QuotePostPageParserDialogFragment.TAG)
                } else {
                    PostListActivity.start(this, threadLink)
                    finish()
                }
            } else {
                ThreadLinkInvalidPromptDialogFragment.newInstance(this,
                        getString(R.string.dialog_message_invalid_or_unsupported_link)).show(
                        supportFragmentManager, ThreadLinkInvalidPromptDialogFragment.TAG)
            }
        }
    }

    companion object {
        private const val ARG_THREAD_LINK = "thread_link"
        fun start(context: Context, uri: Uri) {
            val intent = Intent(context, PostListGatewayActivity::class.java)
            intent.data = uri
            context.startActivity(intent)
        }

        fun start(context: Context, threadLink: ThreadLink) {
            val intent = Intent(context, PostListGatewayActivity::class.java)
            intent.putExtra(ARG_THREAD_LINK, threadLink)
            context.startActivity(intent)
        }
    }
}
