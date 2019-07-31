package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.github.ykrank.androidtools.util.L;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.FriendListFragment;
import me.ykrank.s1next.widget.track.event.ViewUserFriendsTrackEvent;

/**
 * Created by ykrank on 2017/1/16.
 */

public class FriendListActivity extends BaseActivity {

    private static final String ARG_UID = "uid";
    private static final String ARG_USERNAME = "username";

    public static void start(Context context, String uid, String userName) {
        Intent intent = new Intent(context, FriendListActivity.class);
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
        trackAgent.post(new ViewUserFriendsTrackEvent(uid, name));
        L.leaveMsg("FriendListActivity##uid:" + uid + ",name:" + name);

        if (savedInstanceState == null) {
            Fragment fragment = FriendListFragment.Companion.newInstance(uid);
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragment,
                    FriendListFragment.Companion.getTAG()).commit();
        }
    }
}
