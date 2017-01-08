package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.databinding.ActivityHomeBinding;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.util.ViewUtil;

/**
 * Created by ykrank on 2017/1/8.
 */

public class UserHomeActivity extends BaseActivity {

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

        ViewUtil.marginTranslucentToolbar(binding.toolbar);

        init();
    }

    @Override
    public boolean isTranslucent() {
        return true;
    }

    private void init() {
        s1Service.getProfile(uid)
                .compose(RxJavaUtil.iOTransformer())
                .subscribe(wrapper -> {
                    binding.setData(wrapper.getData());
                }, L::e);
    }
}
