package cl.monsoon.s1next.activity;

import android.app.Fragment;
import android.os.Bundle;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.LoginFragment;

public final class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        setNavDrawerEnabled(false);
        setupNavCrossIcon();

        if (savedInstanceState == null) {
            Fragment fragment = new LoginFragment();

            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment, LoginFragment.TAG).commit();
        }
    }
}
