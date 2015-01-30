package cl.monsoon.s1next.activity;

import android.os.Bundle;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.DownloadSettingsFragment;
import cl.monsoon.s1next.fragment.SettingsFragment;

public final class SettingsActivity extends BaseActivity
        implements SettingsFragment.onDownloadPreferenceSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        setNavDrawerEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new SettingsFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 1) {
            getFragmentManager().popBackStack();
            setTitle(R.string.settings);

            return;
        } else if (backStackEntryCount > 1) {
            throw
                    new IllegalStateException(
                            "backStackEntryCount can't be " + backStackEntryCount + ".");
        }

        super.onBackPressed();
    }

    public void onDownloadPreferenceSelected() {
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new DownloadSettingsFragment(), DownloadSettingsFragment.TAG)
                .addToBackStack(null).commit();
        setTitle(R.string.download_settings);
    }
}
