package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.view.View;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.databinding.ActivityHomeBinding;
import me.ykrank.s1next.util.AnimUtils;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.widget.AppBarOffsetChangedListener;

/**
 * Created by ykrank on 2017/1/8.
 */

public class UserHomeActivity extends BaseActivity {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.71f;
    private static final int TITLE_ANIMATIONS_DURATION = 200;

    private static final String ARG_UID = "uid";

    @Inject
    S1Service s1Service;

    private ActivityHomeBinding binding;
    private String uid;

    public static void start(Context context, String uid) {
        Intent intent = new Intent(context, UserHomeActivity.class);
        intent.putExtra(ARG_UID, uid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getPrefComponent(this).inject(this);

        uid = getIntent().getStringExtra(ARG_UID);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        binding.appBar.addOnOffsetChangedListener(new AppBarOffsetChangedListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, int oldVerticalOffset, int verticalOffset) {
                int maxScroll = appBarLayout.getTotalScrollRange();
                float oldPercentage = (float) Math.abs(oldVerticalOffset) / (float) maxScroll;
                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
                if (oldPercentage < PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR && percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
                    //Move up
                    AnimUtils.startAlphaAnimation(binding.toolbarTitle, TITLE_ANIMATIONS_DURATION, View.VISIBLE);
                } else if (oldPercentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR && percentage < PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
                    //Move down
                    AnimUtils.startAlphaAnimation(binding.toolbarTitle, TITLE_ANIMATIONS_DURATION, View.INVISIBLE);
                }
                L.d("verticalOffset:" + verticalOffset + ", percentage:" + percentage);
            }
        });

        loadData();
    }

    @Override
    public boolean isTranslucent() {
        return true;
    }

    private void loadData() {
        s1Service.getProfile(uid)
                .compose(RxJavaUtil.iOTransformer())
                .subscribe(wrapper -> {
                    binding.setData(wrapper.getData());
                }, L::e);
    }
}
