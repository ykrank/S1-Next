package cl.monsoon.s1next.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.activity.PostListActivity;
import cl.monsoon.s1next.adapter.ThreadListRecyclerAdapter;
import cl.monsoon.s1next.model.list.ThreadList;
import cl.monsoon.s1next.model.mapper.ThreadListWrapper;
import cl.monsoon.s1next.util.ObjectUtil;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.MyRecyclerView;
import cl.monsoon.s1next.widget.RecyclerViewHelper;

/**
 * A Fragment representing one of the pages of threads.
 * All activities containing this Fragment must
 * implement {@link cl.monsoon.s1next.fragment.ThreadListPagerFragment.OnPagerInteractionCallback}.
 */
public final class ThreadListPagerFragment extends BaseFragment<ThreadListWrapper> {

    private static final String ARG_FORUM_ID = "forum_id";
    private static final String ARG_PAGE_NUM = "page_num";

    private CharSequence mForumId;
    private int mPageNum;

    private ThreadListRecyclerAdapter mRecyclerAdapter;

    private OnPagerInteractionCallback mOnPagerInteractionCallback;

    public static ThreadListPagerFragment newInstance(CharSequence forumId, int page) {
        ThreadListPagerFragment fragment = new ThreadListPagerFragment();

        Bundle bundle = new Bundle();
        bundle.putCharSequence(ARG_FORUM_ID, forumId);
        bundle.putInt(ARG_PAGE_NUM, page);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mForumId = getArguments().getCharSequence(ARG_FORUM_ID);
        mPageNum = getArguments().getInt(ARG_PAGE_NUM);

        MyRecyclerView recyclerView = (MyRecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new ThreadListRecyclerAdapter();
        recyclerView.setAdapter(mRecyclerAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerViewHelper(
                        getActivity(),
                        recyclerView,
                        new RecyclerViewHelper.OnItemClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {
                                startActivity(view, position, false);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                // cause NullPointerException sometimes when orientation changes
                                try {
                                    startActivity(view, position, true);
                                } catch (NullPointerException ignore) {

                                }
                            }

                            private void startActivity(View view, int position, boolean shouldGoToLastPage) {
                                // user has not permission to access this thread
                                if (!view.isEnabled()) {
                                    return;
                                }

                                Intent intent = new Intent(
                                        ThreadListPagerFragment.this.getActivity(),
                                        PostListActivity.class);

                                cl.monsoon.s1next.model.Thread thread =
                                        mRecyclerAdapter.getItem(position);

                                intent.putExtra(PostListActivity.ARG_THREAD_TITLE, thread.getTitle())
                                        .putExtra(PostListActivity.ARG_THREAD_ID, thread.getId())
                                        .putExtra(PostListActivity.ARG_POST_REPLIES, thread.getReplies() + 1);

                                if (shouldGoToLastPage) {
                                    intent.putExtra(PostListActivity.ARG_SHOULD_GO_TO_LAST_PAGE, true);
                                }

                                ThreadListPagerFragment.this.startActivity(intent);
                            }
                        }
                ));

        setupRecyclerViewPadding(
                recyclerView,
                getResources().getDimensionPixelSize(R.dimen.list_view_padding),
                true);
        enableToolbarAndFabAutoHideEffect(recyclerView, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mOnPagerInteractionCallback =
                ObjectUtil.cast(getFragmentManager().findFragmentByTag(
                        ThreadListFragment.TAG), OnPagerInteractionCallback.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnPagerInteractionCallback = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_thread, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Api.getUrlBrowserThreadList(mForumId, mPageNum)));

                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        execute(Api.getUrlThreadList(mForumId, mPageNum), ThreadListWrapper.class);
    }

    @Override
    public void onPostExecute(AsyncResult<ThreadListWrapper> asyncResult) {
        super.onPostExecute(asyncResult);

        if (asyncResult.exception != null) {
            if (getUserVisibleHint()) {
                AsyncResult.handleException(asyncResult.exception);
            }
        } else {
            ThreadList threadList = asyncResult.data.unwrap();

            // when user has logged out and then has not permission to access this forum
            if (threadList.getData().isEmpty()) {
                String message = asyncResult.data.getResult().getMessage();
                if (!TextUtils.isEmpty(message)) {
                    ToastUtil.showByText(message, Toast.LENGTH_SHORT);
                }
            } else {
                mRecyclerAdapter.setDataSet(threadList.getData());
                mRecyclerAdapter.notifyDataSetChanged();

                mOnPagerInteractionCallback.setTotalPages(
                        threadList.getThreadsInfo().getThreads());
            }
        }
    }

    /**
     * A callback interface that all activities containing this Fragment must implement.
     */
    public static interface OnPagerInteractionCallback {

        /**
         * Callback to set actual total pages which used for {@link android.support.v4.view.PagerAdapter}
         */
        public void setTotalPages(int i);
    }
}
