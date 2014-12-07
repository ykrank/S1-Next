package cl.monsoon.s1next.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.activity.ThreadListActivity;
import cl.monsoon.s1next.activity.ToolbarInterface;
import cl.monsoon.s1next.adapter.ForumListRecyclerAdapter;
import cl.monsoon.s1next.model.Forum;
import cl.monsoon.s1next.model.list.ForumGroupList;
import cl.monsoon.s1next.model.mapper.ForumGroupListWrapper;
import cl.monsoon.s1next.util.ToastHelper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.MyRecyclerView;
import cl.monsoon.s1next.widget.RecyclerViewOnItemTouchListener;

/**
 * A Fragment representing forums.
 */
public final class ForumFragment extends BaseFragment<ForumGroupListWrapper>
        implements ToolbarInterface.OnDropDownItemSelectedListener {

    public static final String TAG = "forum_fragment";

    private ForumListRecyclerAdapter mRecyclerAdapter;

    private ForumGroupList mForumGroupList;
    private ToolbarInterface.SpinnerInteractionCallback mToolbarSpinnerInteractionCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyRecyclerView recyclerView = (MyRecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new ForumListRecyclerAdapter();
        recyclerView.setAdapter(mRecyclerAdapter);

        // the forum list's each element has fixed size
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(
                new RecyclerViewOnItemTouchListener(
                        getActivity(),
                        (position) -> {
                            Intent intent = new Intent(
                                    ForumFragment.this.getActivity(),
                                    ThreadListActivity.class);

                            Forum forum = mRecyclerAdapter.getItem(position);
                            intent.putExtra(ThreadListActivity.ARG_FORUM_TITLE, forum.getName());
                            intent.putExtra(ThreadListActivity.ARG_FORUM_ID, forum.getId());
                            intent.putExtra(ThreadListActivity.ARG_THREADS, forum.getThreads());

                            startActivity(intent);
                        })
        );

        setupRecyclerViewPadding(
                recyclerView,
                getResources().getDimensionPixelSize(R.dimen.list_view_padding),
                true);
        enableToolbarAutoHideEffect(recyclerView, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ToolbarInterface.SpinnerInteractionCallback) {
            mToolbarSpinnerInteractionCallback =
                    (ToolbarInterface.SpinnerInteractionCallback) activity;
        } else {
            throw new ClassCastException(
                    getActivity()
                            + " must implement OnSpinnerInteractionListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mToolbarSpinnerInteractionCallback = null;
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
    public void onRefresh() {
        execute(Api.URL_FORUM, ForumGroupListWrapper.class);
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
                mToolbarSpinnerInteractionCallback.setupToolbarDropDown(
                        mForumGroupList.getForumGroupNameList());
            } catch (NullPointerException e) {
                ToastHelper.showByResId(R.string.message_server_error);
            }
        }
    }

    /**
     * Implement {@link cl.monsoon.s1next.activity.ToolbarInterface.OnDropDownItemSelectedListener}.
     * <p>
     * Shows all forums when {@code position == 0} otherwise for each group.
     */
    @Override
    public void OnToolbarDropDownItemSelected(int position) {
        if (position == 0) {
            mRecyclerAdapter.setDataSet(mForumGroupList.getForumList());
        } else {
            // the first position is "全部"
            // so position - 1 to correspond each group
            mRecyclerAdapter.setDataSet(
                    mForumGroupList.getForumGroupList().get(position - 1).getForumList());
        }
    }
}
