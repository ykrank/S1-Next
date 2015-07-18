package cl.monsoon.s1next.view.activity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import cl.monsoon.s1next.BuildConfig;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.databinding.ActivityHelpBinding;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.viewmodel.WebPageViewModel;

/**
 * An Activity shows a help page.
 * <p>
 * Also some related controls are provided in overflow menu:
 * 1.Bring users to our apps to Android marketplaces or Google Play website.
 * 2.See open sources licenses information.
 * 3.See version number.
 */
public final class HelpActivity extends BaseActivity {

    private static final String HELP_PAGE_URL = "http://monsoon.cl/S1-Next/HELP.html";

    // https://developer.android.com/distribute/tools/promote/linking.html#OpeningDetails
    private static final String ANDROID_APP_MARKET_LINK = "market://details?id=%s";
    private static final String ANDROID_WEB_SITE_MARKET_LINK = "http://play.google.com/store/apps/details?id=%s";

    private WebView mWebView;

    public static void startHelpActivity(Context context) {
        Intent intent = new Intent(context, HelpActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHelpBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_help);

        WebPageViewModel viewModel = new WebPageViewModel();
        binding.setWebPageViewModel(viewModel);
        mWebView = binding.webView;
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                viewModel.finishedLoading.set(true);
            }
        });

        // restore the state of WebView on configuration change
        // see http://www.devahead.com/blog/2012/01/preserving-the-state-of-an-android-webview-on-screen-orientation-change/
        if (savedInstanceState == null) {
            mWebView.loadUrl(HELP_PAGE_URL);
        } else {
            mWebView.restoreState(savedInstanceState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_help, menu);

        menu.findItem(R.id.menu_version).setTitle(getString(R.string.menu_version,
                BuildConfig.VERSION_NAME));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_view_in_google_play_store:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String packageName = getPackageName();
                intent.setData(Uri.parse(String.format(ANDROID_APP_MARKET_LINK, packageName)));
                try {
                    // link our app in Android marketplaces
                    startActivity(intent);
                } catch (ActivityNotFoundException exception) {
                    intent.setData(Uri.parse(String.format(ANDROID_WEB_SITE_MARKET_LINK, packageName)));
                    try {
                        // link our app in Google Play website if user hasn't installed any Android marketplaces
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // show Toast if user hasn't installed any Android marketplaces or browsers
                        ToastUtil.showByResId(R.string.message_chooser_no_applications,
                                Toast.LENGTH_SHORT);
                    }
                }

                return true;
            case R.id.menu_open_source_licenses:
                OpenSourceLicensesActivity.startOpenSourceLicensesActivity(this);

                return true;
            case R.id.menu_version:
                // copy version number to clipboard though it make no sense actually
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(
                        Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("simple text", item.getTitle());
                clipboardManager.setPrimaryClip(clipData);
                ToastUtil.showByResId(R.string.message_version_number_copied, Toast.LENGTH_SHORT);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mWebView.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
