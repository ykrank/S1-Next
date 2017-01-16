package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import me.ykrank.s1next.R;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.fragment.FriendListFragment;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * Created by ykrank on 2017/1/16.
 */

public class FriendListActivity extends BaseActivity {

    private static final String ARG_UID = "uid";

    public static void start(Context context, String uid) {
        Intent intent = new Intent(context, FriendListActivity.class);
        intent.putExtra(ARG_UID, uid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);
        String uid = getIntent().getStringExtra(ARG_UID);
        L.leaveMsg("FriendListActivity##uid:" + uid);

        if (savedInstanceState == null) {
            Fragment fragment = FriendListFragment.newInstance(uid);
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragment,
                    FriendListFragment.TAG).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(this, "好友列表-FriendListActivity"));
    }

    @Override
    protected void onPause() {
        trackAgent.post(new PageEndEvent(this, "好友列表-FriendListActivity"));
        super.onPause();
    }
}
