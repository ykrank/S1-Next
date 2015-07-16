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
import cl.monsoon.s1next.view.dialog.OpenSourceLicensesDialogFragment;
import cl.monsoon.s1next.viewmodel.WebPageViewModel;

public final class HelpActivity extends BaseActivity {

    private static final String HELP_PAGE_URL = "http://monsoon.cl/S1-Next/HELP.html";

    private static final String ANDROID_APP_MARKET_LINK = "market://details?id=%s";
    private static final String ANDROID_WEB_SITE_MARKET_LINK = "http://play.google.com/store/apps/details?id=%s";

    private WebView mWebView;

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
                    startActivity(intent);
                } catch (ActivityNotFoundException exception) {
                    intent.setData(Uri.parse(String.format(ANDROID_WEB_SITE_MARKET_LINK, packageName)));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        ToastUtil.showByResId(R.string.message_chooser_no_applications,
                                Toast.LENGTH_SHORT);
                    }
                }

                return true;
            case R.id.menu_open_source_licenses:
                OpenSourceLicensesDialogFragment.showOpenSourceLicensesDialog(this);

                return true;
            case R.id.menu_version:
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
