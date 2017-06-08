package me.ykrank.s1next.view.fragment

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.databinding.FragmentWebviewBinding
import me.ykrank.s1next.util.L
import me.ykrank.s1next.util.WebViewUtils
import me.ykrank.s1next.view.internal.BackPressDelegate
import me.ykrank.s1next.viewmodel.WebPageViewModel
import java.net.CookieManager
import javax.inject.Inject

/**
 * Local WebView for PC web site and sync OkHttp cookie
 * Created by ykrank on 2017/6/8.
 */
class WebViewFragment : BaseFragment(), BackPressDelegate {
    private lateinit var url: String
    private var enableJs: Boolean = false

    @Inject
    internal lateinit var mCookieManager: CookieManager

    private lateinit var binding: FragmentWebviewBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        App.getAppComponent().inject(this)
        binding = DataBindingUtil.inflate<FragmentWebviewBinding>(inflater, R.layout.fragment_webview, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.leaveMsg(TAG)
        url = arguments.getString(ARG_URL)
        enableJs = arguments.getBoolean(ARG_ENABLE_JS)

        binding.webPageViewModel = WebPageViewModel()

        initWebViewSetting()
        initWebViewClient()

        // restore the state of WebView when configuration changes
        // see http://www.devahead.com/blog/2012/01/preserving-the-state-of-an-android-webview-on-screen-orientation-change/
        if (savedInstanceState == null) {
            binding.webView.loadUrl(url)
        } else {
            binding.webView.restoreState(savedInstanceState)
            // if we haven't finished loading before
            if (binding.webView.url == null) {
                binding.webView.loadUrl(url)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        binding.webView.saveState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.layoutRoot.removeAllViews()
        if (binding.webView != null) {
            binding.webView.clearHistory()
            binding.webView.loadUrl("about:blank")
            binding.webView.freeMemory()
            binding.webView.pauseTimers()
        }
    }

    override fun onBackPressed(): Boolean {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }
        return false
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSetting() {
        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = enableJs
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.setSupportZoom(true)  //支持缩放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        } else {
            webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        }
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.userAgentString = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:51.0) Gecko/20100101 Firefox/51.0"
    }

    private fun initWebViewClient() {
        binding.webView.setWebViewClient(object : CookieSyncWebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                binding.webPageViewModel.setFinishedLoading(true)
                super.onPageFinished(view, url)
            }
        })

        binding.webView.setWebChromeClient(ProgressWebChromeClient(binding.progressBar))

        WebViewUtils.syncWebViewCookies(context, mCookieManager.cookieStore)
    }

    companion object {
        val TAG: String = WebLoginFragment::class.java.name
        val ARG_URL = "arg_url"
        var ARG_ENABLE_JS = "arg_enable_js"

        fun getInstance(url: String, enableJS: Boolean = false): WebViewFragment {
            val fragment = WebViewFragment()
            val bundle = Bundle()
            bundle.putString(ARG_URL, url)
            bundle.putBoolean(ARG_ENABLE_JS, enableJS)
            fragment.arguments = bundle
            return fragment
        }
    }
}

open class CookieSyncWebViewClient : WebViewClient() {

}

open class ProgressWebChromeClient(private val mProgressBar: ProgressBar) : WebChromeClient() {

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        if (newProgress == 100) {
            mProgressBar.visibility = View.INVISIBLE
        } else {
            if (View.INVISIBLE == mProgressBar.visibility) {
                mProgressBar.visibility = View.VISIBLE
            }
            mProgressBar.progress = newProgress
        }
        super.onProgressChanged(view, newProgress)
    }
}