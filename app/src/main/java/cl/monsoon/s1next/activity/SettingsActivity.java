package cl.monsoon.s1next.activity;

import android.os.Bundle;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.DownloadSettingsFragment;
import cl.monsoon.s1next.fragment.SettingsFragment;

public final class SettingsActivity extends BaseActivity {

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
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();

            return;
        }

        super.onBackPressed();
    }

    public void onNestedDownloadPreferenceSelected() {
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new DownloadSettingsFragment(), DownloadSettingsFragment.TAG)
                .addToBackStack(null).commit();
    }
}
