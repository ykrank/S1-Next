package me.ykrank.s1next.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import me.ykrank.s1next.App.Companion.get
import me.ykrank.s1next.R
import me.ykrank.s1next.view.fragment.HelpFragment
import me.ykrank.s1next.view.internal.ToolbarDelegate
import me.ykrank.s1next.widget.track.event.ViewHelpTrackEvent

/**
 * An Activity shows a help page.
 */
class HelpActivity : AppCompatActivity() {
    private var mToolbarDelegate: ToolbarDelegate? = null
    private var mHelpFragment: HelpFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val styleId = intent.getIntExtra(ARG_STYLE, -1)
        if (styleId != -1) {
            setTheme(styleId)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect)
        setupToolbar()
        if (savedInstanceState == null) {
            val instance = HelpFragment.instance
            mHelpFragment = instance
            supportFragmentManager.beginTransaction().add(
                R.id.frame_layout, instance,
                HelpFragment.TAG
            ).commit()
        } else {
            mHelpFragment = supportFragmentManager.findFragmentByTag(
                HelpFragment.TAG
            ) as HelpFragment?
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val webView = mHelpFragment?.webView
        if (webView?.canGoBack() == true) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        if (toolbar != null) {
            mToolbarDelegate = ToolbarDelegate(this, toolbar)
        }
    }

    companion object {
        private const val ARG_STYLE = "style"
        fun startHelpActivity(context: Context, @StyleRes styleId: Int) {
            get().trackAgent.post(ViewHelpTrackEvent())
            val intent = Intent(context, HelpActivity::class.java)
            intent.putExtra(ARG_STYLE, styleId)
            context.startActivity(intent)
        }
    }
}
