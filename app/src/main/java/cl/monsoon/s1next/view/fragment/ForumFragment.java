package cl.monsoon.s1next.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Forum;
import cl.monsoon.s1next.model.list.ForumGroups;
import cl.monsoon.s1next.model.mapper.ForumGroupsWrapper;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.view.activity.ThreadListActivity;
import cl.monsoon.s1next.view.activity.ToolbarInterface;
import cl.monsoon.s1next.view.adapter.ForumListRecyclerAdapter;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpGetLoader;
import cl.monsoon.s1next.widget.RecyclerViewHelper;

/**
 * A Fragment representing forums.
 */
public final class ForumFragment extends BaseFragment<ForumGroupsWrapper>
        implements ToolbarInterface.OnDropDownItemSelectedListener {

    public static final String TAG = ForumFragment.class.getSimpleName();

    private ForumListRecyclerAdapter mRecyclerAdapter;

    private ForumGroups mForumGroups;
    private ToolbarInterface.SpinnerCallback mToolbarSpinnerCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new ForumListRecyclerAdapter();
        recyclerView.setAdapter(mRecyclerAdapter);

        // the forum list's each element has fixed size
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(new RecyclerViewHelper(getActivity(), recyclerView,
                        new RecyclerViewHelper.OnItemClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {
                                Forum forum = mRecyclerAdapter.getItem(position);

                                Intent intent = new Intent(getActivity(), ThreadListActivity.class);
                                intent.putExtra(ThreadListActivity.ARG_FORUM, forum);

                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {

                            }
                        })
        );
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mToolbarSpinnerCallback = (ToolbarInterface.SpinnerCallback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mToolbarSpinnerCallback = null;
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
                IntentUtil.startViewIntentExcludeOurApp(getActivity(), Uri.parse(Api.URL_S1));

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<AsyncResult<ForumGroupsWrapper>> onCreateLoader(int id, Bundle args) {
        super.onCreateLoader(id, args);

        return new HttpGetLoader<>(getActivity(), Api.URL_FORUM, ForumGroupsWrapper.class);
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<ForumGroupsWrapper>> loader, AsyncResult<ForumGroupsWrapper> asyncResult) {
        super.onLoadFinished(loader, asyncResult);

        if (asyncResult.exception != null) {
            ToastUtil.showByResId(asyncResult.getExceptionStringRes(), Toast.LENGTH_SHORT);
        } else {
            ForumGroupsWrapper wrapper = asyncResult.data;
            mForumGroups = wrapper.getForumGroups();

            // after set adapter, host activity
            // would call onToolbarDropDownItemSelected(int).
            mToolbarSpinnerCallback.setupToolbarDropDown(mForumGroups.getForumGroupNameList());
        }
    }

    /**
     * Implements {@link cl.monsoon.s1next.view.activity.ToolbarInterface.OnDropDownItemSelectedListener}.
     * <p>
     * Show all forums when {@code position == 0} otherwise for each group.
     */
    @Override
    public void onToolbarDropDownItemSelected(int position) {
        if (position == 0) {
            mRecyclerAdapter.setDataSet(mForumGroups.getForumList());
        } else {
            // the first position is "全部"
            // so position - 1 to correspond its group
            mRecyclerAdapter.setDataSet(mForumGroups.getForumGroupList().get(position - 1)
                    .getForumList());
        }
        mRecyclerAdapter.notifyDataSetChanged();
    }
}
