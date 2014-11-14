package cl.monsoon.s1next.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.webkit.WebView;

import cl.monsoon.s1next.Config;
import cl.monsoon.s1next.R;

public final class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    public static final String KEY_PREF_NIGHT_MODE = "pref_night_mode";
    public static final String KEY_PREF_DOWNLOAD_AVATARS = "pref_key_download_avatars";
    public static final String KEY_PREF_DOWNLOAD_IMAGES = "pref_key_download_images";

    private static final String KEY_PREF_OPEN_SOURCE_LICENSES = "pref_key_open_source_licenses";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        findPreference(KEY_PREF_OPEN_SOURCE_LICENSES).setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            // set current strategy
            case KEY_PREF_NIGHT_MODE:
                if (sharedPreferences.getBoolean(
                        key, getResources().getBoolean(R.bool.pref_night_mode_default_value))) {
                    Config.setTheme(Config.DARK_THEME);
                } else {
                    Config.setTheme(Config.LIGHT_THEME);
                }

                getActivity().recreate();
                break;
            // change download strategy
            case KEY_PREF_DOWNLOAD_AVATARS:
                Config.setAvatarsDownloadStrategy(sharedPreferences);
                break;
            case KEY_PREF_DOWNLOAD_IMAGES:
                Config.setImagesDownloadStrategy(sharedPreferences);
                break;
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        new OpenSourceLicensesDialog().show(
                getFragmentManager(), OpenSourceLicensesDialog.TAG);

        return true;
    }

    public static class OpenSourceLicensesDialog extends DialogFragment {

        private static final String TAG = "open_source_licenses_dialog";

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            WebView webView = new WebView(getActivity());
            webView.loadUrl("file:///android_asset/NOTICE.html");

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.pref_open_source_licenses)
                    .setView(webView)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                    .create();
        }
    }
}
