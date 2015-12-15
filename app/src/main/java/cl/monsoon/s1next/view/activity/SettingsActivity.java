package cl.monsoon.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.view.fragment.DownloadPreferenceFragment;
import cl.monsoon.s1next.view.fragment.GeneralPreferenceFragment;

/**
 * An Activity includes settings that allow users
 * to modify our app features and behaviors.
 */
public final class SettingsActivity extends BaseActivity {

    private static final String ARG_SHOW_DOWNLOAD_SETTINGS = "show_download_settings";

    public static void startSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void startDownloadSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(ARG_SHOW_DOWNLOAD_SETTINGS, true);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);

        if (savedInstanceState == null) {
            if (getIntent().getBooleanExtra(ARG_SHOW_DOWNLOAD_SETTINGS, false)) {
                setTitle(R.string.pref_downloads);
                getFragmentManager().beginTransaction().replace(R.id.frame_layout,
                        new DownloadPreferenceFragment()).commit();
            } else {
                getFragmentManager().beginTransaction().replace(R.id.frame_layout,
                        new GeneralPreferenceFragment()).commit();
            }
        }
    }
}
