package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import me.ykrank.s1next.R;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.fragment.UserThreadFragment;
import me.ykrank.s1next.widget.track.event.ViewUserThreadTrackEvent;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * Created by ykrank on 2017/2/4.
 */

public class UserThreadActivity extends BaseActivity {
    private static final String ARG_UID = "uid";
    private static final String ARG_USERNAME = "username";
    
    private Fragment fragment;

    public static void start(Context context, String uid, String userName) {
        Intent intent = new Intent(context, UserThreadActivity.class);
        intent.putExtra(ARG_UID, uid);
        intent.putExtra(ARG_USERNAME, userName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);

        String uid = getIntent().getStringExtra(ARG_UID);
        String name = getIntent().getStringExtra(ARG_USERNAME);
        trackAgent.post(new ViewUserThreadTrackEvent(uid, name));
        L.leaveMsg("UserThreadActivity##uid:" + uid + ",name:" + name);
        setTitle(getString(R.string.title_user_threads, name));

        if (savedInstanceState == null) {
            fragment = UserThreadFragment.newInstance(uid);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, fragment, UserThreadFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(this, "用户发帖列表-UserThreadActivity"));
    }

    @Override
    protected void onPause() {
        trackAgent.post(new PageEndEvent(this, "用户发帖列表-UserThreadActivity"));
        super.onPause();
    }
}
