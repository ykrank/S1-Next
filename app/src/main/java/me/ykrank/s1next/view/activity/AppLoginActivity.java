package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.AppLoginFragment;
import me.ykrank.s1next.view.internal.RequestCode;

public final class AppLoginActivity extends BaseActivity{

    public static void startLoginActivityForResultMessage(Activity activity) {
        Intent intent = new Intent(activity, AppLoginActivity.class);
        activity.startActivityForResult(intent, RequestCode.REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, new AppLoginFragment(),
                    AppLoginFragment.TAG).commit();
        }
    }
}
