package cl.monsoon.s1next.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.activity.PostListActivity;
import cl.monsoon.s1next.adapter.ThreadListRecyclerAdapter;
import cl.monsoon.s1next.model.Thread;
import cl.monsoon.s1next.model.list.ThreadList;
import cl.monsoon.s1next.model.mapper.ThreadListWrapper;
import cl.monsoon.s1next.util.ToastHelper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.RecyclerViewOnItemTouchListener;

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

        Bundle args = new Bundle();
        args.putCharSequence(ARG_FORUM_ID, forumId);
        args.putInt(ARG_PAGE_NUM, page);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mForumId = getArguments().getCharSequence(ARG_FORUM_ID);
        mPageNum = getArguments().getInt(ARG_PAGE_NUM);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new ThreadListRecyclerAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);

        int padding = getResources().getDimensionPixelSize(R.dimen.list_view_padding);
        mRecyclerView.setPadding(0, padding, 0, padding);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerViewOnItemTouchListener(
                        getActivity(),
                        (position) -> {
                            Intent intent = new Intent(
                                    ThreadListPagerFragment.this.getActivity(),
                                    PostListActivity.class);

                            Thread thread = mRecyclerAdapter.getItem(position);
                            intent.putExtra(PostListActivity.ARG_THREAD_TITLE, thread.getTitle());
                            intent.putExtra(PostListActivity.ARG_THREAD_ID, thread.getId());
                            intent.putExtra(
                                    PostListActivity.ARG_POST_REPLIES,
                                    thread.getReplies() + 1);

                            startActivity(intent);
                        })
        );
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnPagerInteractionCallback) {
            mOnPagerInteractionCallback = ((OnPagerInteractionCallback) getActivity());
        } else {
            throw new ClassCastException(
                    getActivity()
                            + " must implement OnPagerInteractionListener.");
        }
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
            if (isVisible()) {
                AsyncResult.handleException(asyncResult.exception);
            }
        } else {
            try {
                ThreadList threadList = asyncResult.data.unwrap();
                mRecyclerAdapter.setDataSet(threadList.getThreadList());
                mRecyclerAdapter.notifyDataSetChanged();

                mOnPagerInteractionCallback.setCount(threadList.getThreadsInfo().getThreads());
            } catch (NullPointerException e) {
                ToastHelper.showByResId(R.string.message_server_error);
            }
        }
    }

    /**
     * A callback interface that all activities containing this Fragment must implement.
     */
    public static interface OnPagerInteractionCallback {

        /**
         * Callback to set actual page which used for {@link android.support.v4.view.PagerAdapter}
         */
        public void setCount(int i);
    }
}
