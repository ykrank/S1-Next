package cl.monsoon.s1next.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import cl.monsoon.s1next.adapter.PostListRecyclerAdapter;
import cl.monsoon.s1next.model.list.PostList;
import cl.monsoon.s1next.model.mapper.PostListWrapper;
import cl.monsoon.s1next.util.ObjectUtil;
import cl.monsoon.s1next.util.StringHelper;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.MyRecyclerView;

/**
 * A Fragment which includes {@link android.support.v4.view.ViewPager}
 * to represent each page of thread posts.
 * <p>
 * All activities containing this Fragment must implement
 * {@link cl.monsoon.s1next.fragment.PostListPagerFragment.OnPagerInteractionCallback}.
 * <p>
 * Similar to {@link cl.monsoon.s1next.fragment.ThreadListPagerFragment}
 */
public final class PostListPagerFragment extends BaseFragment<PostListWrapper> {

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_PAGE_NUM = "page_num";

    /**
     * The serialization (saved instance state) Bundle key representing whether
     * {@link cl.monsoon.s1next.widget.MyRecyclerView} is loading more when configuration changes.
     */
    private static final String STATE_IS_LOADING_MORE = "is_loading_more";

    private String mThreadId;
    private int mPageNum;

    private MyRecyclerView mRecyclerView;
    private PostListRecyclerAdapter mRecyclerAdapter;
    private boolean mIsLoadingMore;

    private OnPagerInteractionCallback mOnPagerInteractionCallback;

    public static PostListPagerFragment newInstance(String threadId, int page) {
        PostListPagerFragment fragment = new PostListPagerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_THREAD_ID, threadId);
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

        mThreadId = getArguments().getString(ARG_THREAD_ID);
        mPageNum = getArguments().getInt(ARG_PAGE_NUM);

        mRecyclerView = (MyRecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        // linearLayoutManager.setSmoothScrollbarEnabled(false);
        // if https://code.google.com/p/android/issues/detail?id=78375&q=setSmoothScrollbarEnabled&colspec=ID%20Type%20Status%20Owner%20Summary%20Stars has fixed
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerAdapter = new PostListRecyclerAdapter(getActivity());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        onInsetsChanged();
        enableToolbarAndFabAutoHideEffect(mRecyclerView, new RecyclerView.OnScrollListener() {

            /**
             * Endless scrolling with RecyclerView.
             */
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!mIsLoadingMore
                        && mPageNum == mOnPagerInteractionCallback.getTotalPages()
                        && !mRecyclerView.canScrollVertically(1)
                        && !isRefreshing()) {

                    mIsLoadingMore = true;
                    setSwipeRefreshLayoutEnabled(false);
                    mRecyclerAdapter.showFooterProgress();
                    onRefresh();
                }
            }
        });

        if (savedInstanceState != null) {
            mIsLoadingMore = savedInstanceState.getBoolean(STATE_IS_LOADING_MORE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mOnPagerInteractionCallback = ObjectUtil.cast(activity, OnPagerInteractionCallback.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnPagerInteractionCallback = null;
    }

    @Override
    public void onInsetsChanged(@NonNull Rect insets) {
        setRecyclerViewPadding(
                mRecyclerView,
                insets,
                getResources().getDimensionPixelSize(R.dimen.recycler_view_card_padding));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_post, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_browser:
                String url = Api.getPostListUrlForBrowser(mThreadId, mPageNum);

                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                startActivity(intent);

                return true;
            case R.id.menu_share:
                String value =
                        StringHelper.concatWithTwoSpaces(
                                getThreadTitle(), Api.getPostListUrlForBrowser(mThreadId, 1));

                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, value);
                intent.setType("text/plain");

                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_IS_LOADING_MORE, mIsLoadingMore);
    }

    private CharSequence getThreadTitle() {
        CharSequence title = getActivity().getTitle();
        // remove two space and page number's length
        return title.subSequence(0, title.length() - 2 - String.valueOf(mPageNum).length());
    }

    @Override
    public void onRefresh() {
        execute(Api.getPostListUrl(mThreadId, mPageNum), PostListWrapper.class);
    }

    @Override
    public void onPostExecute(AsyncResult<PostListWrapper> asyncResult) {
        super.onPostExecute(asyncResult);

        boolean isFinishedLoadingMore = false;
        if (mIsLoadingMore) {
            // mRecyclerAdapter.getItemCount() = 0
            // when configuration changes (like orientation changes)
            if (mRecyclerAdapter.getItemCount() == 0) {
                setSwipeRefreshLayoutEnabled(false);
            } else {
                mRecyclerAdapter.hideFooterProgress();
                mIsLoadingMore = false;
                isFinishedLoadingMore = true;
            }
        }

        if (asyncResult.exception != null) {
            if (getUserVisibleHint()) {
                asyncResult.handleException();
            }
        } else {
            PostList postList = asyncResult.data.unwrap();

            // if user has logged out and then has no permission to access this thread
            if (postList.getData().isEmpty()) {
                String message = asyncResult.data.getResult().getMessage();
                if (!TextUtils.isEmpty(message)) {
                    ToastUtil.showByText(message, Toast.LENGTH_SHORT);
                }
            } else {
                String title = postList.getInfo().getTitle();
                ((PostListActivity)getActivity()).setmThreadTitle(title, mPageNum);
                int lastItemCount = mRecyclerAdapter.getItemCount();
                mRecyclerAdapter.setDataSet(postList.getData());
                if (isFinishedLoadingMore) {
                    int newItemCount = mRecyclerAdapter.getItemCount() - lastItemCount;
                    if (newItemCount > 0) {
                        mRecyclerAdapter.notifyItemRangeInserted(lastItemCount, newItemCount);
                    }
                } else {
                    mRecyclerAdapter.notifyDataSetChanged();
                }

                mOnPagerInteractionCallback.setTotalPages(postList.getInfo().getReplies() + 1);
            }


            if (postList.getThreadAttachment() != null) {
                ObjectUtil.cast(getActivity(),
                        PostListActivity.class).setupThreadAttachment(postList.getThreadAttachment());
            }
        }

        if (mIsLoadingMore) {
            mRecyclerAdapter.showFooterProgress();
        }
    }

    /**
     * A callback interface that all activities containing this Fragment must implement.
     */
    public static interface OnPagerInteractionCallback {

        public int getTotalPages();

        /**
         * A callback to set actual total pages which used for {@link android.support.v4.view.PagerAdapter}.
         */
        public void setTotalPages(int i);
    }
}
