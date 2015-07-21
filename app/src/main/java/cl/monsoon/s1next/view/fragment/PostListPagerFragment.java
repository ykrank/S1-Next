package cl.monsoon.s1next.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Post;
import cl.monsoon.s1next.data.api.model.collection.Posts;
import cl.monsoon.s1next.data.api.model.wrapper.PostsWrapper;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.util.StringUtil;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.view.adapter.PostListRecyclerViewAdapter;
import rx.Observable;

/**
 * A Fragment which includes {@link android.support.v4.view.ViewPager}
 * to represent each page of thread posts.
 * <p>
 * All activities containing this Fragment must implement {@link PagerCallback}.
 * <p>
 * Similar to {@link cl.monsoon.s1next.view.fragment.ThreadListPagerFragment}.
 */
public final class PostListPagerFragment extends BaseFragment<PostsWrapper> {

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_PAGE_NUM = "page_num";

    /**
     * Used for quote post redirect.
     */
    private static final String ARG_QUOTE_POST_ID = "quote_post_id";

    /**
     * The serialization (saved instance state) Bundle key representing whether
     * {@link RecyclerView} is loading more when configuration changes.
     */
    private static final String STATE_IS_LOADING_MORE = "is_loading_more";

    private String mThreadId;
    private int mPageNum;

    private RecyclerView mRecyclerView;
    private PostListRecyclerViewAdapter mRecyclerAdapter;
    private boolean mIsLoadingMore;

    private PagerCallback mPagerCallback;

    public static PostListPagerFragment newInstance(String threadId, int page) {
        return newInstance(threadId, page, null);
    }

    public static PostListPagerFragment newInstance(String threadId, int pageNum, String postId) {
        PostListPagerFragment fragment = new PostListPagerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_THREAD_ID, threadId);
        if (!TextUtils.isEmpty(postId)) {
            bundle.putString(ARG_QUOTE_POST_ID, postId);
        }
        bundle.putInt(ARG_PAGE_NUM, pageNum);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mThreadId = getArguments().getString(ARG_THREAD_ID);
        mPageNum = getArguments().getInt(ARG_PAGE_NUM);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // we need to adjust RecyclerView's top/bottom padding
        // because its child views (CardView) has top/bottom margin
        int padding = getResources().getDimensionPixelSize(R.dimen.recycler_view_padding_for_card);
        mRecyclerView.setPadding(0, padding, 0, padding);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new PostListRecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            /**
             * Endless scrolling with RecyclerView.
             */
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!mIsLoadingMore
                        && mPageNum == mPagerCallback.getTotalPages()
                        && mRecyclerAdapter.getItemCount() != 0
                        && !mRecyclerView.canScrollVertically(1)
                        && !isLoading()) {

                    mIsLoadingMore = true;
                    mRecyclerAdapter.showFooterProgress();
                    startPullToRefresh();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // We need retrieve mIsLoadingMore back before initializing Loader.
        if (savedInstanceState != null) {
            mIsLoadingMore = savedInstanceState.getBoolean(STATE_IS_LOADING_MORE);
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mPagerCallback = (PagerCallback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mPagerCallback = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_post_pager, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_browser:
                IntentUtil.startViewIntentExcludeOurApp(getActivity(),
                        Uri.parse(Api.getPostListUrlForBrowser(mThreadId, mPageNum)));

                return true;
            case R.id.menu_share:
                String value;
                CharSequence title = mPagerCallback.getThreadTitle();
                String url = Api.getPostListUrlForBrowser(mThreadId, mPageNum);
                if (TextUtils.isEmpty(title)) {
                    value = url;
                } else {
                    value = StringUtil.concatWithTwoSpaces(title, url);
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, value);
                intent.setType("text/plain");

                startActivity(Intent.createChooser(intent, getString(R.string.menu_title_share)));

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_IS_LOADING_MORE, mIsLoadingMore);
    }

    @Override
    Observable<PostsWrapper> getSourceObservable() {
        return mS1Service.getPostsWrapper(mThreadId, mPageNum);
    }

    @Override
    void onNext(PostsWrapper data) {
        super.onNext(data);

        boolean isFinishedLoadingMore = false;
        if (mIsLoadingMore) {
            // mRecyclerAdapter.getItemCount() = 0
            // when configuration changes (like orientation changes)
            if (mRecyclerAdapter.getItemCount() != 0) {
                mRecyclerAdapter.hideFooterProgress();
                mIsLoadingMore = false;
                isFinishedLoadingMore = true;
            }
        }

        Posts posts = data.getPosts();
        List<Post> postList = posts.getPostList();

        // if user has logged out, has no permission to access this thread or this thread is invalid
        if (postList.isEmpty()) {
            String message = data.getResult().getMessage();
            if (!TextUtils.isEmpty(message)) {
                ToastUtil.showByText(message, Toast.LENGTH_SHORT);
            }
        } else {
            int lastItemCount = mRecyclerAdapter.getItemCount();
            mRecyclerAdapter.setDataSet(postList);
            if (isFinishedLoadingMore) {
                int newItemCount = mRecyclerAdapter.getItemCount() - lastItemCount;
                if (newItemCount > 0) {
                    mRecyclerAdapter.notifyItemRangeInserted(lastItemCount, newItemCount);
                }
            } else {
                mRecyclerAdapter.notifyDataSetChanged();

                String quotePostId = getArguments().getString(ARG_QUOTE_POST_ID);
                if (!TextUtils.isEmpty(quotePostId)) {
                    for (int i = 0, length = postList.size(); i < length; i++) {
                        if (quotePostId.equals(postList.get(i).getId())) {
                            // scroll to quote post
                            mRecyclerView.scrollToPosition(i);
                            break;
                        }
                    }
                    // clear this argument after redirect
                    getArguments().putString(ARG_QUOTE_POST_ID, null);
                }
            }

            cl.monsoon.s1next.data.api.model.Thread postListInfo = posts.getPostListInfo();
            // we have not title if we open thread link in our app
            if (TextUtils.isEmpty(getActivity().getTitle())) {
                mPagerCallback.setThreadTitle(postListInfo.getTitle());
            }
            mPagerCallback.setTotalPageByPosts(postListInfo.getReplies() + 1);
        }

        if (posts.getThreadAttachment() != null) {
            mPagerCallback.setupThreadAttachment(posts.getThreadAttachment());
        }
    }

    @Override
    void onError(Throwable throwable) {
        if (mIsLoadingMore) {
            mRecyclerAdapter.hideFooterProgress();
            mIsLoadingMore = false;
        }

        if (getUserVisibleHint()) {
            super.onError(throwable);
        }
    }

    /**
     * A callback interface that all activities containing this Fragment must implement.
     */
    public interface PagerCallback {

        int getTotalPages();

        /**
         * A callback to set actual total pages which used for {@link android.support.v4.view.PagerAdapter}.
         */
        void setTotalPageByPosts(int threads);

        CharSequence getThreadTitle();

        void setThreadTitle(CharSequence title);

        void setupThreadAttachment(Posts.ThreadAttachment threadAttachment);
    }
}
