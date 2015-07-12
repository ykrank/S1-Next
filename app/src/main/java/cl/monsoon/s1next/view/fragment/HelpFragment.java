package cl.monsoon.s1next.view.fragment;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import cl.monsoon.s1next.BuildConfig;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.util.ToastUtil;

public final class HelpFragment extends Fragment {

    public static final String TAG = HelpFragment.class.getSimpleName();

    private static final String mURL = "http://monsoon.cl/S1-Next/HELP.html";

    private WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWebView = (WebView) view.findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView webView, String url) {
                view.findViewById(R.id.progress_bar).setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });

        if (savedInstanceState == null) {
            mWebView.loadUrl(mURL);
        } else {
            mWebView.restoreState(savedInstanceState);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_help, menu);
        menu.findItem(R.id.menu_version).setTitle(getString(R.string.menu_version,
                BuildConfig.VERSION_NAME));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_view_in_google_play_store:
                String packageName = getActivity().getPackageName();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + packageName));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException exception) {
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="
                            + packageName));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        ToastUtil.showByResId(R.string.message_chooser_no_applications,
                                Toast.LENGTH_SHORT);
                    }
                }

                return true;
            case R.id.menu_open_source_licenses:
                new OpenSourceLicensesDialogFragment().show(getFragmentManager(),
                        OpenSourceLicensesDialogFragment.TAG);

                return true;
            case R.id.menu_version:
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(
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

    public WebView getWebView() {
        return mWebView;
    }

    public static class OpenSourceLicensesDialogFragment extends DialogFragment {

        private static final String TAG = OpenSourceLicensesDialogFragment.class.getSimpleName();

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            WebView webView = new WebView(getActivity());
            webView.loadUrl("file:///android_asset/NOTICE.html");

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.menu_open_source_licenses)
                    .setView(webView)
                    .setPositiveButton(R.string.dialog_button_text_done, (dialog, which) -> dialog.dismiss())
                    .create();
        }
    }
}
