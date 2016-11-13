package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.PmFragment;

/**
 * Created by ykrank on 2016/11/12 0012.
 */

public class PmActivity extends BaseActivity{
    public static void startPmActivity(Context context) {
        Intent intent = new Intent(context, PmActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if (savedInstanceState == null) {
            Fragment fragment = new PmFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragment,
                    PmFragment.TAG).commit();
        }
    }
}
