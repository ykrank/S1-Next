package cl.monsoon.s1next.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.data.api.model.ThreadLink;
import cl.monsoon.s1next.view.fragment.PostListFragment;

/**
 * An Activity which includes {@link android.support.v4.view.ViewPager}
 * to represent each page of post lists.
 */
public final class PostListActivity extends BaseActivity {

    private static final String ARG_THREAD = "thread";
    private static final String ARG_SHOULD_GO_TO_LAST_PAGE = "should_go_to_last_page";

    private static final String ARG_THREAD_LINK = "thread_link";

    public static void startPostListActivity(Context context, Thread thread, boolean shouldGoToLastPage) {
        Intent intent = new Intent(context, PostListActivity.class);
        intent.putExtra(ARG_THREAD, thread);
        intent.putExtra(ARG_SHOULD_GO_TO_LAST_PAGE, shouldGoToLastPage);

        context.startActivity(intent);
    }

    public static void startPostListActivity(Activity activity, ThreadLink threadLink) {
        Intent intent = new Intent(activity, PostListActivity.class);
        intent.putExtra(ARG_THREAD_LINK, threadLink);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        disableDrawerIndicator();

        if (savedInstanceState == null) {
            Fragment fragment;
            Intent intent = getIntent();
            Thread thread = intent.getParcelableExtra(ARG_THREAD);
            if (thread == null) {
                fragment = PostListFragment.newInstance(this, intent.getParcelableExtra(
                        ARG_THREAD_LINK));
            } else {
                fragment = PostListFragment.newInstance(thread, intent.getBooleanExtra(
                        ARG_SHOULD_GO_TO_LAST_PAGE, false));
            }
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragment,
                    PostListFragment.TAG).commit();
        }
    }
}
