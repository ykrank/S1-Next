package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.NewRateFragment;
import me.ykrank.s1next.view.fragment.NewThreadFragment;

/**
 * An Activity which used to send a reply.
 */
public final class NewRateActivity extends BaseActivity {

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_POST_ID = "post_id";

    private NewRateFragment mFragment;

    public static void start(Activity activity, String threadId, String postId) {
        Intent intent = new Intent(activity, NewRateActivity.class);
        intent.putExtra(ARG_THREAD_ID, threadId);
        intent.putExtra(ARG_POST_ID, postId);

        BaseActivity.Companion.startActivityForResultMessage(activity, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);

        Intent intent = getIntent();
        String threadId = intent.getStringExtra(ARG_THREAD_ID);
        String postID = intent.getStringExtra(ARG_POST_ID);
        setTitle(R.string.title_new_rate);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(NewThreadFragment.Companion.getTAG());
        if (fragment == null) {
            mFragment = NewRateFragment.Companion.newInstance(threadId, postID);
            fragmentManager.beginTransaction().add(R.id.frame_layout, mFragment,
                    NewThreadFragment.Companion.getTAG()).commit();
        } else {
            mFragment = (NewRateFragment) fragment;
        }
    }
}
