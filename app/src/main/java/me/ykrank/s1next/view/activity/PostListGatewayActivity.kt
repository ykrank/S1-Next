package me.ykrank.s1next.view.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.ThreadLink
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
class PostListGatewayActivity : androidx.fragment.app.FragmentActivity() {

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
            val uri = intent.data
            val threadLink = ThreadLink.parse(uri.toString())
            if (threadLink.isPresent) {
                val threadLinkInstance = threadLink.get()
                if (threadLinkInstance.quotePostId.isPresent) {
                    QuotePostPageParserDialogFragment.newInstance(threadLinkInstance).show(
                            supportFragmentManager, QuotePostPageParserDialogFragment.TAG)
                } else {
                    PostListActivity.start(this, threadLinkInstance)
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

        fun start(context: Context, uri: Uri) {
            val intent = Intent(context, PostListGatewayActivity::class.java)
            intent.data = uri
            context.startActivity(intent)
        }
    }
}
