package cl.monsoon.s1next.activity;

import android.os.Bundle;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.DownloadPreferenceFragment;
import cl.monsoon.s1next.fragment.MainPreferenceFragment;

public final class SettingsActivity extends BaseActivity {

    public static final String ARG_SHOULD_SHOW_DOWNLOAD_SETTINGS = "should_show_download_settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer);

        if (savedInstanceState == null) {
            if (getIntent().getBooleanExtra(ARG_SHOULD_SHOW_DOWNLOAD_SETTINGS, false)) {
                setTitle(R.string.download_settings);
                getFragmentManager().beginTransaction().replace(R.id.frame_layout,
                        new DownloadPreferenceFragment()).commit();
            } else {
                getFragmentManager().beginTransaction().replace(R.id.frame_layout,
                        new MainPreferenceFragment()).commit();
            }
        }
    }
}
