package cl.monsoon.s1next.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.view.fragment.LoginFragment;

public final class LoginActivity extends BaseActivity {

    public static void startLoginActivityForResultMessage(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        BaseActivity.startActivityForResultMessage(activity, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, new LoginFragment(),
                    LoginFragment.TAG).commit();
        }
    }
}
