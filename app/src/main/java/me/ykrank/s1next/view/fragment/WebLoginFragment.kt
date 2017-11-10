package me.ykrank.s1next.view.fragment

import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.WebViewUtils
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.databinding.FragmentWebviewBinding
import me.ykrank.s1next.view.activity.ForumActivity
import me.ykrank.s1next.viewmodel.WebPageViewModel
import me.ykrank.s1next.widget.hostcheck.AppHostUrl
import java.net.URI
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * A Fragment to login in WebView.
 */
class WebLoginFragment : BaseFragment() {

    @Inject
    internal lateinit var cookieManger: java.net.CookieManager
    @Inject
    internal lateinit var baseHostUrl: AppHostUrl

    private lateinit var mFragmentHelpBinding: FragmentWebviewBinding
    private var webView: WebView? = null
    private lateinit var mProgressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        App.appComponent.inject(this)
        mFragmentHelpBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_webview, container,
                false)
        webView = mFragmentHelpBinding.webView
        mProgressBar = mFragmentHelpBinding.progressBar

        return mFragmentHelpBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.leaveMsg(WebLoginFragment::class.java.name)

        WebViewUtils.clearWebViewCookies(context)

        val viewModel = WebPageViewModel()
        mFragmentHelpBinding.webPageViewModel = viewModel

        initWebViewSetting()

        webView?.webViewClient = object : CookieWebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                viewModel.setFinishedLoading(true)
                super.onPageFinished(view, url)
            }
        }

        webView?.webChromeClient = ProgressWebChromeClient(mProgressBar)

        val baseUrl: String = baseHostUrl.baseHttpUrl?.toString() ?: Api.BASE_URL

        webView?.let {
            // restore the state of WebView when configuration changes
            // see http://www.devahead.com/blog/2012/01/preserving-the-state-of-an-android-webview-on-screen-orientation-change/
            if (savedInstanceState == null) {
                it.loadUrl(baseUrl + LOGIN_PAGE_URL_SUFFIX)
            } else {
                it.restoreState(savedInstanceState)
                // if we haven't finished loading before
                if (it.url == null) {
                    it.loadUrl(baseUrl + LOGIN_PAGE_URL_SUFFIX)
                }
            }
        }

        //Only one webView instance in application, so we should resume timers because we stop it onDestroy
        webView?.resumeTimers()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        webView?.saveState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mFragmentHelpBinding.layoutRoot.removeAllViews()
        webView?.let {
            it.clearHistory()
            it.loadUrl("about:blank")
            it.freeMemory()
            it.pauseTimers()
            webView = null
        }
    }

    private fun initWebViewSetting() {
        val webSettings = webView?.settings
        if (webSettings != null) {
            webSettings.javaScriptEnabled = true
            webSettings.cacheMode = WebSettings.LOAD_NO_CACHE//不使用缓存，只从网络获取数据.
            webSettings.setSupportZoom(true)  //支持缩放
            webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN //支持内容重新布局
            webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //Since Lollipop (API 21), WebView blocks all mixed content by default.
                //But login page need load mixed content
                webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            }
        }
    }

    private fun isLogged(cookieStr: String?): Boolean {
        val pattern = Pattern.compile("\\w+_auth=(\\w|%)+;")
        return cookieStr != null && pattern.matcher(cookieStr.replace(" ", "")).find()
    }

    private open inner class CookieWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            try {
                val activity = activity
                if (activity != null) {
                    //update okHttp cookie with WebView cookie
                    val manager = CookieManager.getInstance()
                    val cookieStr = manager.getCookie(url)
                    if (isLogged(cookieStr)) {
                        val uri = URI.create(url)
                        val cookieMap = HashMap<String, List<String>>()
                        val list = ArrayList<String>()
                        list.addAll(Arrays.asList(*cookieStr.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
                        cookieMap.put("Set-Cookie", list)
                        cookieManger.cookieStore.removeAll()
                        cookieManger.put(uri, cookieMap)
                        //Login success
                        ForumActivity.start(activity)
                    }
                }
            } catch (e: Exception) {
                L.report(e)
            }

        }
    }

    companion object {

        val TAG = WebLoginFragment::class.java.name

        private val LOGIN_PAGE_URL_SUFFIX = "forum-27-1.html"

        val instance: WebLoginFragment
            get() = WebLoginFragment()
    }
}
