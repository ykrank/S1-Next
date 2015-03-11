package cl.monsoon.s1next.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.activity.PostListActivity;
import cl.monsoon.s1next.activity.SubForumPostListActivity;
import cl.monsoon.s1next.activity.SubForumThreadListActivity;
import cl.monsoon.s1next.adapter.ThreadListRecyclerAdapter;
import cl.monsoon.s1next.model.Forum;
import cl.monsoon.s1next.model.list.ThreadList;
import cl.monsoon.s1next.model.mapper.ThreadListWrapper;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.MyRecyclerView;
import cl.monsoon.s1next.widget.RecyclerViewHelper;

/**
 * A Fragment representing one of the pages of threads.
 * <p>
 * All activities containing this Fragment must implement {@link PagerCallback}.
 */
public final class ThreadListPagerFragment extends BaseFragment<ThreadListWrapper> {

    private static final String ARG_FORUM_ID = "forum_id";
    private static final String ARG_PAGE_NUM = "page_num";

    private String mForumId;
    private int mPageNum;

    private MyRecyclerView mRecyclerView;
    private ThreadListRecyclerAdapter mRecyclerAdapter;

    private PagerCallback mPageCallback;
    private SubFormsCallback mSubFormsCallback;

    public static ThreadListPagerFragment newInstance(String forumId, int page) {
        ThreadListPagerFragment fragment = new ThreadListPagerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_FORUM_ID, forumId);
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

        mForumId = getArguments().getString(ARG_FORUM_ID);
        mPageNum = getArguments().getInt(ARG_PAGE_NUM);

        mRecyclerView = (MyRecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new ThreadListRecyclerAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerViewHelper(
                        getActivity(),
                        mRecyclerView,
                        new RecyclerViewHelper.OnItemClickListener() {

                            @Override
                            public void onItemClick(@NonNull View view, int position) {
                                startActivity(view, position, false);
                            }

                            @Override
                            public void onItemLongClick(@NonNull View view, int position) {
                                // cause NullPointerException sometimes when orientation changes
                                try {
                                    startActivity(view, position, true);
                                } catch (NullPointerException ignore) {

                                }
                            }

                            private void startActivity(View view, int position, boolean shouldGoToLastPage) {
                                // if user has no permission to access this thread
                                if (!view.isEnabled()) {
                                    return;
                                }

                                Intent intent;
                                if (getActivity() instanceof SubForumThreadListActivity) {
                                    intent =
                                            new Intent(
                                                    getActivity(), SubForumPostListActivity.class);
                                } else {
                                    intent = new Intent(getActivity(), PostListActivity.class);
                                }

                                cl.monsoon.s1next.model.Thread thread =
                                        mRecyclerAdapter.getItem(position);

                                // same to SubForumPostListActivity.ARG_THREAD
                                intent.putExtra(PostListActivity.ARG_THREAD, thread);

                                if (shouldGoToLastPage) {
                                    // same to SubForumPostListActivity.ARG_SHOULD_GO_TO_LAST_PAGE
                                    intent.putExtra(PostListActivity.ARG_SHOULD_GO_TO_LAST_PAGE, true);
                                }

                                ThreadListPagerFragment.this.startActivity(intent);
                            }
                        }
                ));


        onInsetsChanged();
        enableToolbarAndFabAutoHideEffect(mRecyclerView, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mPageCallback =
                (PagerCallback) getFragmentManager().findFragmentByTag(ThreadListFragment.TAG);
        mSubFormsCallback = (SubFormsCallback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mPageCallback = null;
        mSubFormsCallback = null;
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

        inflater.inflate(R.menu.fragment_thread, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Api.getThreadListUrlForBrowser(mForumId, mPageNum)));

                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        execute(Api.getThreadListUrl(mForumId, mPageNum), ThreadListWrapper.class);
    }

    @Override
    public void onPostExecute(AsyncResult<ThreadListWrapper> asyncResult) {
        super.onPostExecute(asyncResult);

        if (asyncResult.exception != null) {
            if (getUserVisibleHint()) {
                asyncResult.handleException();
            }
        } else {
            ThreadList threadList = asyncResult.data.unwrap();

            // if user has logged out and then has no permission to access this forum
            if (threadList.getData().isEmpty()) {
                String message = asyncResult.data.getResult().getMessage();
                if (!TextUtils.isEmpty(message)) {
                    ToastUtil.showByText(message, Toast.LENGTH_SHORT);
                }
            } else {
                mRecyclerAdapter.setDataSet(threadList.getData());
                mRecyclerAdapter.notifyDataSetChanged();

                mPageCallback.setTotalPages(threadList.getThreadsInfo().getThreads());
            }

            if (!threadList.getSubForumList().isEmpty()) {
                mSubFormsCallback.setupSubForums(threadList.getSubForumList());
            }
        }
    }

    /**
     * A callback interface that all activities containing this Fragment must implement.
     */
    public interface PagerCallback {

        /**
         * A callback to set actual total pages which used for {@link android.support.v4.view.PagerAdapter}。
         */
        void setTotalPages(int i);
    }

    public interface SubFormsCallback {

        void setupSubForums(List<Forum> forumList);
    }
}
