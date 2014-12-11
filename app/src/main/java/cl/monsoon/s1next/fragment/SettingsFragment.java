package cl.monsoon.s1next.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.StringDef;
import android.support.v4.content.LocalBroadcastManager;
import android.webkit.WebView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.singleton.Config;

public final class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({PREF_KEY_FONT_SIZE, PREF_KEY_DOWNLOAD_AVATARS, PREF_KEY_DOWNLOAD_IMAGES})
    public @interface PreferenceKey {

    }

    public static final String PREF_KEY_NIGHT_MODE = "pref_night_mode";
    public static final String PREF_KEY_FONT_SIZE = "pref_key_font_size";
    public static final String PREF_KEY_DOWNLOAD_AVATARS = "pref_key_download_avatars";
    public static final String PREF_KEY_DOWNLOAD_IMAGES = "pref_key_download_images";

    public static final String ACTION_CHANGE_THEME = "change_theme";
    public static final String ACTION_CHANGE_FONT_SIZE = "change_font_size";

    private static final String PREF_KEY_OPEN_SOURCE_LICENSES = "pref_key_open_source_licenses";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        findPreference(PREF_KEY_OPEN_SOURCE_LICENSES).setOnPreferenceClickListener(this);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @PreferenceKey String key) {
        switch (key) {
            // set current strategy
            case PREF_KEY_NIGHT_MODE:
                if (sharedPreferences.getBoolean(
                        key, getResources().getBoolean(R.bool.pref_night_mode_default_value))) {
                    Config.setCurrentTheme(Config.DARK_THEME);
                } else {
                    Config.setCurrentTheme(Config.LIGHT_THEME);
                }

                LocalBroadcastManager.getInstance(getActivity())
                        .sendBroadcast(new Intent(ACTION_CHANGE_THEME));
                break;
            // change font size
            case PREF_KEY_FONT_SIZE:
                Config.setTextSize(sharedPreferences);

                LocalBroadcastManager.getInstance(getActivity())
                        .sendBroadcast(new Intent(ACTION_CHANGE_FONT_SIZE));
                break;
            // change download strategy
            case PREF_KEY_DOWNLOAD_AVATARS:
                Config.setAvatarsDownloadStrategy(sharedPreferences);
                break;
            case PREF_KEY_DOWNLOAD_IMAGES:
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
