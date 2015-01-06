package cl.monsoon.s1next.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.ThreadListFragment;

public final class ThreadListActivity extends BaseActivity {

    public static final String ARG_FORUM_TITLE = "forum_title";
    public static final String ARG_FORUM_ID = "forum_id";
    public static final String ARG_THREADS = "threads";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        enableWindowTranslucentStatus();
        setNavDrawerIndicatorEnabled(false);

        if (savedInstanceState == null) {
            Fragment fragment = ThreadListFragment.newInstance(
                    getIntent().getCharSequenceExtra(ARG_FORUM_TITLE),
                    getIntent().getCharSequenceExtra(ARG_FORUM_ID),
                    getIntent().getIntExtra(ARG_THREADS, 1));

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment, ThreadListFragment.TAG).commit();
        }
    }
}
