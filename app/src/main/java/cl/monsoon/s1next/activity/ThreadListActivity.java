package cl.monsoon.s1next.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.adapter.SubForumArrayAdapter;
import cl.monsoon.s1next.fragment.ThreadListFragment;
import cl.monsoon.s1next.fragment.ThreadListPagerFragment;
import cl.monsoon.s1next.model.Forum;

public final class ThreadListActivity extends BaseActivity implements ThreadListPagerFragment.SubForumsCallback {

    public static final String ARG_FORUM = "forum";

    /**
     * Only measure this many items to get a decent max width.
     */
    private static final int MAX_ITEMS_MEASURED = 15;

    private MenuItem mMenuSubForums;

    private ListPopupWindow mListPopupWindow;
    private SubForumArrayAdapter mSubForumArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        setNavDrawerIndicatorEnabled(false);

        if (savedInstanceState == null) {
            Fragment fragment = ThreadListFragment.newInstance(getIntent().getParcelableExtra(
                    ARG_FORUM));

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment,
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sub_forums:
                mListPopupWindow.setAnchorView(getToolbar().findViewById(R.id.menu_sub_forums));
                mListPopupWindow.show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setupSubForums(List<Forum> forumList) {
        if (mListPopupWindow == null) {
            mListPopupWindow = new ListPopupWindow(this);
            mListPopupWindow.setContentWidth(ListPopupWindow.MATCH_PARENT);

            mSubForumArrayAdapter = new SubForumArrayAdapter(this,
                    R.layout.item_popup_menu_dropdown, forumList);
            mListPopupWindow.setAdapter(mSubForumArrayAdapter);
            mListPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
                Forum forum = mSubForumArrayAdapter.getItem(position);

                // we use the same activity (ThreadListActivity) for sub forum
                Intent intent = new Intent(ThreadListActivity.this, ThreadListActivity.class);
                intent.putExtra(ThreadListActivity.ARG_FORUM, forum);

                startActivity(intent);

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
        for (int i = start; i < end; i++) {
            int positionType = spinnerAdapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            itemView = spinnerAdapter.getView(i, itemView, getToolbar());
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
