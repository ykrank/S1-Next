package cl.monsoon.s1next.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.webkit.WebView;

import cl.monsoon.s1next.R;

public final class OpenSourceLicensesDialogFragment extends DialogFragment {

    private static final String TAG = OpenSourceLicensesDialogFragment.class.getName();

    public static void showOpenSourceLicensesDialog(FragmentActivity fragmentActivity) {
        new OpenSourceLicensesDialogFragment().show(fragmentActivity.getSupportFragmentManager(),
                OpenSourceLicensesDialogFragment.TAG);
    }

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
