package cl.monsoon.s1next.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.webkit.WebView;

import cl.monsoon.s1next.BuildConfig;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.activity.SettingsActivity;
import cl.monsoon.s1next.singleton.Config;

public final class SettingsFragment extends BaseSettingsFragment
        implements Preference.OnPreferenceClickListener {

    public static final String PREF_KEY_THEME = "pref_key_theme";
    public static final String PREF_KEY_FONT_SIZE = "pref_key_font_size";

    public static final String ACTION_CHANGE_THEME = "change_theme";
    public static final String ACTION_CHANGE_FONT_SIZE = "change_font_size";

    private static final String PREF_KEY_DOWNLOADS = "pref_key_downloads";

    private static final String PREF_KEY_OPEN_SOURCE_LICENSES = "pref_key_open_source_licenses";
    private static final String PREF_KEY_VERSION = "pref_key_version";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.main_preferences);

        findPreference(PREF_KEY_DOWNLOADS).setOnPreferenceClickListener(this);
        findPreference(PREF_KEY_OPEN_SOURCE_LICENSES).setOnPreferenceClickListener(this);

        findPreference(PREF_KEY_VERSION).setSummary(
                getResources().getString(R.string.pref_version_summary, BuildConfig.VERSION_NAME));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            // set current theme
            case PREF_KEY_THEME:
                Config.setCurrentTheme(sharedPreferences);
                getActivity().sendBroadcast(new Intent(ACTION_CHANGE_THEME));

                break;
            // change font size
            case PREF_KEY_FONT_SIZE:
                Config.setTextScale(sharedPreferences);
                getActivity().sendBroadcast(new Intent(ACTION_CHANGE_FONT_SIZE));

                break;
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case PREF_KEY_DOWNLOADS:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra(SettingsActivity.ARG_SHOULD_SHOW_DOWNLOAD_SETTINGS, true);
                startActivity(intent);

                break;
            case PREF_KEY_OPEN_SOURCE_LICENSES:
                new OpenSourceLicensesDialog().show(
                        getFragmentManager(), OpenSourceLicensesDialog.TAG);

                break;
        }

        return true;
    }

    public static class OpenSourceLicensesDialog extends DialogFragment {

        private static final String TAG = OpenSourceLicensesDialog.class.getSimpleName();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            WebView webView = new WebView(getActivity());
            webView.loadUrl("file:///android_asset/NOTICE.html");

            return
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.pref_open_source_licenses)
                            .setView(webView)
                            .setPositiveButton(
                                    android.R.string.ok,
                                    (dialog, which) -> dialog.dismiss())
                            .create();
        }
    }
}
