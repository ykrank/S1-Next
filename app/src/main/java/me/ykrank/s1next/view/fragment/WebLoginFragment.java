package me.ykrank.s1next.view.fragment;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.databinding.FragmentWebviewBinding;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.WebViewUtils;
import me.ykrank.s1next.view.activity.ForumActivity;
import me.ykrank.s1next.viewmodel.WebPageViewModel;
import me.ykrank.s1next.widget.hostcheck.BaseHostUrl;

/**
 * A Fragment to login in WebView.
 */
public final class WebLoginFragment extends BaseFragment {

    public static final String TAG = WebLoginFragment.class.getName();

    private static final String LOGIN_PAGE_URL_SUFFIX = "forum-27-1.html";

    @Inject
    java.net.CookieManager cookieManger;
    @Inject
    BaseHostUrl baseHostUrl;

    private FragmentWebviewBinding mFragmentHelpBinding;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static WebLoginFragment getInstance() {
        return new WebLoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        mFragmentHelpBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_webview, container,
                false);
        mWebView = mFragmentHelpBinding.webView;
        mProgressBar = mFragmentHelpBinding.progressBar;

        return mFragmentHelpBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        L.leaveMsg(WebLoginFragment.class.getName());

        WebViewUtils.clearWebViewCookies(getContext());

        WebPageViewModel viewModel = new WebPageViewModel();
        mFragmentHelpBinding.setWebPageViewModel(viewModel);

        initWebViewSetting();

        mWebView.setWebViewClient(new CookieWebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                viewModel.setFinishedLoading(true);
                super.onPageFinished(view, url);
            }
        });

        mWebView.setWebChromeClient(new ProgressWebChromeClient(mProgressBar));

        String baseUrl = baseHostUrl.getBaseUrl();

        // restore the state of WebView when configuration changes
        // see http://www.devahead.com/blog/2012/01/preserving-the-state-of-an-android-webview-on-screen-orientation-change/
        if (savedInstanceState == null) {
            mWebView.loadUrl(baseUrl + LOGIN_PAGE_URL_SUFFIX);
        } else {
            mWebView.restoreState(savedInstanceState);
            // if we haven't finished loading before
            if (mWebView.getUrl() == null) {
                mWebView.loadUrl(baseUrl + LOGIN_PAGE_URL_SUFFIX);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mWebView.saveState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFragmentHelpBinding.layoutRoot.removeAllViews();
        if (mWebView != null) {
            mWebView.clearHistory();
            mWebView.loadUrl("about:blank");
            mWebView.freeMemory();
            mWebView.pauseTimers();
            mWebView = null;
        }
    }

    public WebView getWebView() {
        return mWebView;
    }

    private void initWebViewSetting() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.
        webSettings.setSupportZoom(true);  //支持缩放
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
    }

    private boolean isLogged(String cookieStr) {
        Pattern pattern = Pattern.compile("\\w+_auth=(\\w|%)+;");
        return cookieStr != null && pattern.matcher(cookieStr.replace(" ", "")).find();
    }

    private class CookieWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            try {
                Activity activity = getActivity();
                if (activity != null) {
                    //update okHttp cookie with WebView cookie
                    CookieManager manager = CookieManager.getInstance();
                    String cookieStr = manager.getCookie(url);
                    if (isLogged(cookieStr)) {
                        URI uri = URI.create(url);
                        Map<String, List<String>> cookieMap = new HashMap<>();
                        List<String> list = new ArrayList<>();
                        list.addAll(Arrays.asList(cookieStr.split(";")));
                        cookieMap.put("Set-Cookie", list);
                        cookieManger.getCookieStore().removeAll();
                        cookieManger.put(uri, cookieMap);
                        //Login success
                        ForumActivity.start(activity);
                    }
                }
            } catch (Exception e) {
                L.report(e);
            }
        }
    }
}
