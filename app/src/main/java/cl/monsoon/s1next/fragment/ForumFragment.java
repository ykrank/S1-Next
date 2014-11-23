package cl.monsoon.s1next.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.activity.ThreadListActivity;
import cl.monsoon.s1next.adapter.ForumListRecyclerAdapter;
import cl.monsoon.s1next.model.Forum;
import cl.monsoon.s1next.model.list.ForumGroupList;
import cl.monsoon.s1next.model.mapper.ForumGroupListWrapper;
import cl.monsoon.s1next.util.ToastHelper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.RecyclerItemTouchListener;

/**
 * A Fragment representing forums.
 */
public final class ForumFragment extends AbsNavigationDrawerInteractionFragment<Forum, ForumGroupListWrapper, ForumListRecyclerAdapter.ViewHolder> {

    public static final String TAG = "forum_fragment";

    private ForumGroupList mForumGroupList;

    /**
     * Host Activity callback.
     */
    private OnToolbarSpinnerInteractionCallback mOnToolbarSpinnerInteractionCallback;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int padding = getResources().getDimensionPixelSize(R.dimen.list_view_padding);
        mRecyclerView.setPadding(0, padding, 0, padding);
        // the forum list's each element are fixed size
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemTouchListener(
                        getActivity(),
                        (position) -> {
                            Intent intent = new Intent(
                                    ForumFragment.this.getActivity(),
                                    ThreadListActivity.class);

                            Forum forum = mRecyclerAdapter.getItem(position);
                            intent.putExtra(ThreadListActivity.ARG_FORUM_NAME, forum.getName());
                            intent.putExtra(ThreadListActivity.ARG_FORUM_ID, forum.getId());
                            intent.putExtra(ThreadListActivity.ARG_THREADS, forum.getThreads());

                            startActivity(intent);
                        })
        );
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnToolbarSpinnerInteractionCallback) {
            mOnToolbarSpinnerInteractionCallback = (OnToolbarSpinnerInteractionCallback) activity;
        } else {
            throw new ClassCastException(
                    getActivity()
                            + " must implement OnSpinnerInteractionListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnToolbarSpinnerInteractionCallback = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_forum, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Api.URL_S1));

                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initAdapter() {
        super.initAdapter();

        mRecyclerAdapter = new ForumListRecyclerAdapter();
    }

    @Override
    void load() {
        executeHttpGet(Api.URL_FORUM, ForumGroupListWrapper.class);
    }

    @Override
    public void onPostExecute(AsyncResult<ForumGroupListWrapper> asyncResult) {
        super.onPostExecute(asyncResult);

        if (asyncResult.exception != null) {
            AsyncResult.handleException(asyncResult.exception);
        } else {
            try {
                ForumGroupListWrapper wrapper = asyncResult.data;
                mForumGroupList = wrapper.unwrap();

                // after set adapter, host activity
                // would call changeContent().
                mOnToolbarSpinnerInteractionCallback.setAdapterDataSet(
                        mForumGroupList.getForumGroupNameList());
            } catch (NullPointerException e) {
                ToastHelper.showByResId(R.string.message_server_error);
            }
        }
    }

    /**
     * Set content to All sorted Forum when {@code position == 0}
     * else to group forum.
     */
    public void changeContent() {
        int position = mOnToolbarSpinnerInteractionCallback.getItemPosition();
        if (position == 0) {
            mRecyclerAdapter.setDataSet(mForumGroupList.getForumList());
        } else {
            // the first position is "全部"
            // so position - 1 to correspond each group
            mRecyclerAdapter.setDataSet(
                    mForumGroupList.getForumGroupList().get(position - 1).getForumList());
        }

        mRecyclerAdapter.notifyDataSetChanged();
    }

    /**
     * A callback interface that all activities containing this Fragment must
     * implement.
     */
    public static interface OnToolbarSpinnerInteractionCallback {

        /**
         * Set Spinner data set.
         */
        public void setAdapterDataSet(List<? extends CharSequence> dropDownItem);

        /**
         * Get Spinner drop down item position.
         */
        public int getItemPosition();
    }
}
