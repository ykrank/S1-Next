package cl.monsoon.s1next.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.FavouriteListFragment;

public final class FavouriteListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        if (savedInstanceState == null) {
            Fragment fragment = new FavouriteListFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment,
                    FavouriteListFragment.TAG).commit();
        }
    }
}
