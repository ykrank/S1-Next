package me.ykrank.s1next.view.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.ykrank.androidtools.ui.internal.CoordinatorLayoutAnchorDelegate
import com.github.ykrank.androidtools.util.L.leaveMsg
import com.google.android.material.snackbar.Snackbar
import me.ykrank.s1next.R
import me.ykrank.s1next.databinding.FragmentWebviewBinding
import me.ykrank.s1next.view.activity.OpenSourceLicensesActivity
import me.ykrank.s1next.view.dialog.VersionInfoDialogFragment
import me.ykrank.s1next.viewmodel.WebPageViewModel

/**
 * A Fragment represents a help page.
 *
 *
 * Also some related controls are provided in overflow menu:
 * 1.Link our app to Android marketplaces or Google Play website.
 * 2.See open sources licenses information.
 * 3.See version number.
 */
class HelpFragment : Fragment() {
    private lateinit var mFragmentHelpBinding: FragmentWebviewBinding
    var webView: WebView? = null
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentHelpBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_webview, container,
            false
        )
        webView = mFragmentHelpBinding.webView
        return mFragmentHelpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        leaveMsg("HelpFragment")
        val viewModel = WebPageViewModel()
        mFragmentHelpBinding.setWebPageViewModel(viewModel)
        webView?.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                viewModel.setFinishedLoading(true)
            }
        })

        // restore the state of WebView when configuration changes
        // see http://www.devahead.com/blog/2012/01/preserving-the-state-of-an-android-webview-on-screen-orientation-change/
        if (savedInstanceState == null) {
            webView?.loadUrl(HELP_PAGE_URL)
        } else {
            webView?.restoreState(savedInstanceState)
            // if we haven't finished loading before
            if (webView?.getUrl() == null) {
                webView?.loadUrl(HELP_PAGE_URL)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_help, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.menu_view_in_google_play_store) {
            val intent = Intent(Intent.ACTION_VIEW)
            val packageName = requireContext().packageName
            intent.setData(Uri.parse(String.format(ANDROID_APP_MARKET_LINK, packageName)))
            try {
                // link our app in Android marketplaces
                startActivity(intent)
            } catch (exception: ActivityNotFoundException) {
                intent.setData(Uri.parse(String.format(ANDROID_WEB_SITE_MARKET_LINK, packageName)))
                try {
                    // link our app in Google Play website if user hasn't installed any Android marketplaces
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // show Snackbar if user hasn't installed any Android marketplaces or browsers
                    (activity as CoordinatorLayoutAnchorDelegate?)?.showSnackbar(
                        R.string.message_chooser_no_applications, Snackbar.LENGTH_SHORT
                    )
                }
            }
            return true
        } else if (itemId == R.id.menu_open_source_licenses) {
            OpenSourceLicensesActivity.startOpenSourceLicensesActivity(context)
            return true
        } else if (itemId == R.id.menu_version_info) {
            VersionInfoDialogFragment().show(
                parentFragmentManager,
                VersionInfoDialogFragment.TAG
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView?.saveState(outState)
    }

    companion object {
        @JvmField
        val TAG = HelpFragment::class.java.getName()
        private const val HELP_PAGE_URL = "file:///android_asset/help/HELP.html"

        /**
         * https://developer.android.com/distribute/tools/promote/linking.html#OpeningDetails
         */
        private const val ANDROID_APP_MARKET_LINK = "market://details?id=%s"
        private const val ANDROID_WEB_SITE_MARKET_LINK =
            "https://play.google.com/store/apps/details?id=%s"

        @JvmStatic
        val instance: HelpFragment
            get() = HelpFragment()
    }
}
