package me.ykrank.s1next.view.activity;

import android.support.v7.app.AppCompatActivity;

import me.ykrank.s1next.util.LeaksUtil;

/**
 * Created by ykrank on 2016/12/27.
 */

public abstract class OriginActivity extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LeaksUtil.releaseFastgrabConfigReaderLeaks(this);
    }
}
