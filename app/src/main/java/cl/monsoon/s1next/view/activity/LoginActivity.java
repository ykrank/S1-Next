package cl.monsoon.s1next.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.view.fragment.LoginFragment;

public final class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer);

        if (savedInstanceState == null) {
            Fragment fragment = new LoginFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment,
                    LoginFragment.TAG).commit();
        }
    }
}
