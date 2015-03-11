package cl.monsoon.s1next.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.LoginFragment;

public final class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        setupNavCrossIcon();
        setNavDrawerEnabled(false);

        if (savedInstanceState == null) {
            Fragment fragment = new LoginFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment, LoginFragment.TAG).commit();
        }
    }
}
