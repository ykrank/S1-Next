package cl.monsoon.s1next.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import cl.monsoon.s1next.util.ObjectUtil;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.MyRecyclerView;
import cl.monsoon.s1next.widget.RecyclerViewHelper;

/**
 * A Fragment representing forums.
 */
public final class ForumFragment extends BaseFragment<ForumGroupListWrapper>
        implements ToolbarInterface.OnDropDownItemSelectedListener {

    public static final String TAG = "forum_fragment";

    private MyRecyclerView mRecyclerView;
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

        mRecyclerView = (MyRecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new ForumListRecyclerAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);

        // the forum list's each element has fixed size
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerViewHelper(
                        getActivity(),
                        mRecyclerView,
                        new RecyclerViewHelper.OnItemClickListener() {
                            @Override
                            public void onItemClick(@NonNull View view, int position) {
                                Forum forum = mRecyclerAdapter.getItem(position);

                                Intent intent = new Intent(getActivity(), ThreadListActivity.class);
                                intent.putExtra(ThreadListActivity.ARG_FORUM, forum);

                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(@NonNull View view, int position) {

                            }
                        })
        );

        onInsetsChanged();
        enableToolbarAndFabAutoHideEffect(mRecyclerView, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mToolbarSpinnerInteractionCallback =
                ObjectUtil.cast(activity, ToolbarInterface.SpinnerInteractionCallback.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mToolbarSpinnerInteractionCallback = null;
    }

    @Override
    public void onInsetsChanged(@NonNull Rect insets) {
        setRecyclerViewPadding(
                mRecyclerView,
                insets,
                getResources().getDimensionPixelSize(R.dimen.list_view_padding));
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
            asyncResult.handleException();
        } else {
            ForumGroupListWrapper wrapper = asyncResult.data;
            mForumGroupList = wrapper.unwrap();

            // after set adapter, host activity
            // would call onToolbarDropDownItemSelected(int).
            mToolbarSpinnerInteractionCallback.setupToolbarDropDown(
                    mForumGroupList.getForumGroupNameList());
        }
    }

    /**
     * Implements {@link cl.monsoon.s1next.activity.ToolbarInterface.OnDropDownItemSelectedListener}.
     * <p>
     * Show all forums when {@code position == 0} otherwise for each group.
     */
    @Override
    public void onToolbarDropDownItemSelected(int position) {
        if (position == 0) {
            mRecyclerAdapter.setDataSet(mForumGroupList.getForumList());
        } else {
            // the first position is "全部"
            // so position - 1 to correspond its group
            mRecyclerAdapter.setDataSet(mForumGroupList.getData().get(position - 1).getForumList());
        }
        mRecyclerAdapter.notifyDataSetChanged();
    }
}
