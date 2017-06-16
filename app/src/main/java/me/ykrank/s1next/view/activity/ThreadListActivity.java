package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

import java.util.List;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.api.model.Forum;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.adapter.SubForumArrayAdapter;
import me.ykrank.s1next.view.fragment.ThreadListFragment;
import me.ykrank.s1next.view.fragment.ThreadListPagerFragment;
import me.ykrank.s1next.widget.WifiBroadcastReceiver;
import me.ykrank.s1next.widget.track.event.RandomImageTrackEvent;
import me.ykrank.s1next.widget.track.event.ViewForumTrackEvent;

/**
 * An Activity shows the thread lists.
 */
public final class ThreadListActivity extends BaseActivity
        implements ThreadListPagerFragment.SubForumsCallback, WifiBroadcastReceiver.NeedMonitorWifi {

    private static final String ARG_FORUM = "forum";

    /**
     * Only measures this many items to get a decent max width.
     */
    private static final int MAX_ITEMS_MEASURED = 15;

    private MenuItem mMenuSubForums;
    private ListPopupWindow mListPopupWindow;
    private SubForumArrayAdapter mSubForumArrayAdapter;

    private Forum forum;
    private boolean refreshBlackList = false;

    public static void startThreadListActivity(Context context, Forum forum) {
        Intent intent = new Intent(context, ThreadListActivity.class);
        intent.putExtra(ARG_FORUM, forum);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        disableDrawerIndicator();

        forum = getIntent().getParcelableExtra(ARG_FORUM);
        if (forum == null) {
            L.report(new IllegalStateException("ThreadListActivity intent forum is null"));
            finish();
            return;
        }
        trackAgent.post(new ViewForumTrackEvent(forum.getId(), forum.getName()));
        L.leaveMsg("ThreadListActivity##forum" + forum);

        if (savedInstanceState == null) {
            Fragment fragment = ThreadListFragment.newInstance(forum);
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragment,
                    ThreadListFragment.TAG).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_thread, menu);

        mMenuSubForums = menu.findItem(R.id.menu_sub_forums);
        if (mListPopupWindow == null) {
            mMenuSubForums.setVisible(false);
        }

        MenuItem newThreadMenu = menu.findItem(R.id.menu_new_thread);
        if (!mUser.isLogged()) {
            newThreadMenu.setVisible(false);
        }

        MenuItem randomImageMenu = menu.findItem(R.id.menu_random_image);
        if (forum != null && TextUtils.equals(forum.getId(), "6")) {
            randomImageMenu.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sub_forums:
                mListPopupWindow.setAnchorView(getToolbar().get().findViewById(R.id.menu_sub_forums));
                mListPopupWindow.show();

                return true;
            case R.id.menu_new_thread:
                NewThreadActivity.startNewThreadActivityForResultMessage(this, Integer.parseInt(forum.getId()));
                return true;
            case R.id.menu_random_image:
                trackAgent.post(new RandomImageTrackEvent());
                GalleryActivity.Companion.start(this, Api.randomImage());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (refreshBlackList) {
            showShortSnackbar(R.string.blacklist_refresh_warn);
            refreshBlackList = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PostListActivity.RESULT_BLACKLIST) {
            if (resultCode == RESULT_OK) {
                refreshBlackList = true;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
        refreshBlackList = false;
        super.onPause();
    }

    @Override
    public void setupSubForums(List<Forum> forumList) {
        if (mListPopupWindow == null) {
            mListPopupWindow = new ListPopupWindow(this);

            mSubForumArrayAdapter = new SubForumArrayAdapter(this, R.layout.item_popup_menu_dropdown,
                    forumList);
            mListPopupWindow.setAdapter(mSubForumArrayAdapter);
            mListPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
                // we use the same activity (ThreadListActivity) for sub forum
                ThreadListActivity.startThreadListActivity(this, mSubForumArrayAdapter.getItem(
                        position));

                mListPopupWindow.dismiss();
            });

            mListPopupWindow.setContentWidth(measureContentWidth(mSubForumArrayAdapter));

            // mMenuSubForums = null when configuration changes (like orientation changes)
            // but we don't need to care about the visibility of mMenuSubForums
            // because mListPopupWindow != null and we won't invoke
            // mMenuSubForums.setVisible(false) during onCreateOptionsMenu(Menu)
            if (mMenuSubForums != null) {
                mMenuSubForums.setVisible(true);
            }
        } else {
            mSubForumArrayAdapter.clear();
            mSubForumArrayAdapter.addAll(forumList);
            mSubForumArrayAdapter.notifyDataSetChanged();
        }

        // We need to invoke this every times when mSubForumArrayAdapter changes,
        // but now we only invoke this in the first time due to cost-performance.
        // mListPopupWindow.setContentWidth(measureContentWidth(mSubForumArrayAdapter));
    }

    /**
     * Forked from android.widget.Spinner#measureContentWidth(SpinnerAdapter, Drawable).
     */
    private int measureContentWidth(SpinnerAdapter spinnerAdapter) {
        if (spinnerAdapter == null) {
            return 0;
        }

        int width = 0;
        View itemView = null;
        int itemType = 0;
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        // Make sure the number of items we'll measure is capped.
        // If it's a huge data set with wildly varying sizes, oh well.
        int start = 0;
        int end = Math.min(spinnerAdapter.getCount(), start + MAX_ITEMS_MEASURED);
        int count = end - start;
        start = Math.max(0, start - (MAX_ITEMS_MEASURED - count));
        ViewGroup parent = getToolbar().get();
        for (int i = start; i < end; i++) {
            int positionType = spinnerAdapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            itemView = spinnerAdapter.getView(i, itemView, parent);
            if (itemView.getLayoutParams() == null) {
                itemView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, itemView.getMeasuredWidth());
        }

        return width;
    }
}
