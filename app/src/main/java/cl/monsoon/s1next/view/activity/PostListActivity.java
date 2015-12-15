package cl.monsoon.s1next.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Browser;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.data.api.model.ThreadLink;
import cl.monsoon.s1next.view.fragment.PostListFragment;
import cl.monsoon.s1next.widget.WifiBroadcastReceiver;

/**
 * An Activity which includes {@link android.support.v4.view.ViewPager}
 * to represent each page of post lists.
 */
public final class PostListActivity extends BaseActivity
        implements WifiBroadcastReceiver.NeedMonitorWifi {

    private static final String ARG_THREAD = "thread";
    private static final String ARG_SHOULD_GO_TO_LAST_PAGE = "should_go_to_last_page";

    private static final String ARG_THREAD_LINK = "thread_link";
    private static final String ARG_COME_FROM_OTHER_APP = "come_from_other_app";

    public static void startPostListActivity(Context context, Thread thread, boolean shouldGoToLastPage) {
        Intent intent = new Intent(context, PostListActivity.class);
        intent.putExtra(ARG_THREAD, thread);
        intent.putExtra(ARG_SHOULD_GO_TO_LAST_PAGE, shouldGoToLastPage);

        context.startActivity(intent);
    }

    public static void startPostListActivity(Activity activity, ThreadLink threadLink) {
        startPostListActivity(activity, threadLink, !activity.getPackageName().equals(
                // see android.text.style.URLSpan#onClick(View)
                activity.getIntent().getStringExtra(Browser.EXTRA_APPLICATION_ID)));
    }

    public static void startPostListActivity(Context context, ThreadLink threadLink, boolean comeFromOtherApp) {
        Intent intent = new Intent(context, PostListActivity.class);
        intent.putExtra(ARG_THREAD_LINK, threadLink);
        intent.putExtra(ARG_COME_FROM_OTHER_APP, comeFromOtherApp);

        context.startActivity(intent);
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
                fragment = PostListFragment.newInstance(intent.getParcelableExtra(ARG_THREAD_LINK));
            } else {
                fragment = PostListFragment.newInstance(thread, intent.getBooleanExtra(
                        ARG_SHOULD_GO_TO_LAST_PAGE, false));
            }
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragment,
                    PostListFragment.TAG).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getIntent().getBooleanExtra(ARG_COME_FROM_OTHER_APP, false)) {
                    // this activity is not part of this app's task
                    // so create a new task when navigating up
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(new Intent(this, ForumActivity.class))
                            .startActivities();
                    finish();

                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
