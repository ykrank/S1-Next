package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.LoginFragment;
import me.ykrank.s1next.view.fragment.WebLoginFragment;
import me.ykrank.s1next.view.internal.RequestCode;

public final class LoginActivity extends BaseActivity implements LoginFragment.LoginFragmentCallback {

    public static void startLoginActivityForResultMessage(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(intent, RequestCode.REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, new LoginFragment(),
                    LoginFragment.TAG).commit();
        }
    }

    @Override
    public void loginInWeb() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, WebLoginFragment.getInstance(), WebLoginFragment.TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }
}
