package me.ykrank.s1next.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
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
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.databinding.FragmentWebviewBinding;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.activity.ForumActivity;
import me.ykrank.s1next.viewmodel.WebPageViewModel;
import me.ykrank.s1next.widget.track.event.PageEndEvent;
import me.ykrank.s1next.widget.track.event.PageStartEvent;

/**
 * A Fragment to login in WebView.
 */
public final class WebLoginFragment extends BaseFragment {

    public static final String TAG = WebLoginFragment.class.getName();

    private static final String LOGIN_PAGE_URL = Api.BASE_URL + "forum-27-1.html";

    /**
     * https://developer.android.com/distribute/tools/promote/linking.html#OpeningDetails
     */
    private static final String ANDROID_APP_MARKET_LINK = "market://details?id=%s";
    private static final String ANDROID_WEB_SITE_MARKET_LINK = "http://play.google.com/store/apps/details?id=%s";

    @Inject
    java.net.CookieManager cookieManger;

    private FragmentWebviewBinding mFragmentHelpBinding;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static WebLoginFragment getInstance() {
        return new WebLoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.getAppComponent(getContext()).inject(this);
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

        mWebView.setWebChromeClient(new ProgressWebChromeClient());

        // restore the state of WebView when configuration changes
        // see http://www.devahead.com/blog/2012/01/preserving-the-state-of-an-android-webview-on-screen-orientation-change/
        if (savedInstanceState == null) {
            mWebView.loadUrl(LOGIN_PAGE_URL);
        } else {
            mWebView.restoreState(savedInstanceState);
            // if we haven't finished loading before
            if (mWebView.getUrl() == null) {
                mWebView.loadUrl(LOGIN_PAGE_URL);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mWebView.saveState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent("网页登录-" + TAG));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent("网页登录-" + TAG));
        super.onPause();
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
        Pattern pattern = Pattern.compile("(;|^)s1uid=\\d+;");
        return cookieStr != null && pattern.matcher(cookieStr.replace(" ", "")).find();
    }

    class CookieWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            try {
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
                    ForumActivity.start(getActivity());
                }
            } catch (Exception e) {
                L.e(e);
            }
            super.onPageFinished(view, url);
        }
    }

    class ProgressWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.INVISIBLE);
            } else {
                if (View.INVISIBLE == mProgressBar.getVisibility()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                mProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }
}
