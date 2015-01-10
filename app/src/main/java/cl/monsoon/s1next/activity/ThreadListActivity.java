package cl.monsoon.s1next.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.adapter.SubForumBaseAdapter;
import cl.monsoon.s1next.fragment.ThreadListFragment;
import cl.monsoon.s1next.model.Forum;
import cl.monsoon.s1next.util.ObjectUtil;

/**
 * This Activity has Spinner (if this forum has sub-forums)
 * in ToolBar to switch between thread list and sub-forums.
 */
public class ThreadListActivity extends BaseActivity {

    public static final String ARG_FORUM_TITLE = "forum_title";
    public static final String ARG_FORUM_ID = "forum_id";
    public static final String ARG_THREADS = "threads";

    private SubForumBaseAdapter mSubForumBaseAdapter;

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

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);

        if (mSubForumBaseAdapter != null) {
            mSubForumBaseAdapter.setItemTitle(title);
        }
    }

    /**
     * @see cl.monsoon.s1next.adapter.SubForumBaseAdapter
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            Forum forum = mSubForumBaseAdapter.getItem(position);

            Intent intent = new Intent(this, SubForumThreadListActivity.class);
            intent.putExtra(ThreadListActivity.ARG_FORUM_TITLE, forum.getName())
                    .putExtra(ThreadListActivity.ARG_FORUM_ID, forum.getId())
                    .putExtra(ThreadListActivity.ARG_THREADS, forum.getThreads());

            startActivity(intent);

            // We need to reselect the first dropdown item
            // in order to let us get the correct position of sub-forums later.
            parent.setSelection(0);
        }
    }

    @Override
    BaseAdapter getSpinnerAdapter(List dropDownItemList) {
        List<Forum> list = new ArrayList<>();
        list.addAll(ObjectUtil.uncheckedCast(dropDownItemList));

        mSubForumBaseAdapter = new SubForumBaseAdapter(this, getTitle(), list);

        return mSubForumBaseAdapter;
    }
}
