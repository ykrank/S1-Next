package cl.monsoon.s1next.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Post;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.data.api.model.collection.Posts;
import cl.monsoon.s1next.data.api.model.wrapper.PostsWrapper;
import cl.monsoon.s1next.databinding.FragmentBaseCardViewContainerBinding;
import cl.monsoon.s1next.view.adapter.PostListRecyclerViewAdapter;
import cl.monsoon.s1next.view.internal.LoadingViewModelBindingDelegate;
import cl.monsoon.s1next.view.internal.LoadingViewModelBindingDelegateBaseCardViewContainerImpl;
import rx.Observable;

/**
 * A Fragment representing one of the pages of posts.
 * <p>
 * Activity or Fragment containing this must implement {@link PagerCallback}.
 */
public final class PostListPagerFragment extends BaseFragment<PostsWrapper> {

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_PAGE_NUM = "page_num";

    /**
     * Used for post post redirect.
     */
    private static final String ARG_QUOTE_POST_ID = "quote_post_id";

    private String mThreadId;
    private int mPageNum;
    private boolean blacklistChanged = false;

    private RecyclerView mRecyclerView;
    private PostListRecyclerViewAdapter mRecyclerAdapter;

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

        mRecyclerView = getRecyclerView();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new PostListRecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        // add pull up to refresh to RecyclerView
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!isPullUpToRefresh()
                        && mPageNum == mPagerCallback.getTotalPages()
                        && !isLoading()
                        && mRecyclerAdapter.getItemCount() != 0
                        && !mRecyclerView.canScrollVertically(1)) {
                    startPullToRefresh();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mPagerCallback = (PagerCallback) getFragmentManager().findFragmentByTag(PostListFragment.TAG);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mPagerCallback = null;
    }

    @Override
    LoadingViewModelBindingDelegate getLoadingViewModelBindingDelegateImpl(LayoutInflater inflater, ViewGroup container) {
        FragmentBaseCardViewContainerBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_base_card_view_container, container, false);
        return new LoadingViewModelBindingDelegateBaseCardViewContainerImpl(binding);
    }

    @Override
    void startPullToRefresh() {
        mRecyclerAdapter.showFooterProgress();
        super.startPullToRefresh();
    }

    void startBlackListRefresh() {
        blacklistChanged = true;
        startPullToRefresh();
    }

    @Override
    Observable<PostsWrapper> getSourceObservable() {
        return mS1Service.getPostsWrapper(mThreadId, mPageNum);
    }

    @Override
    void onNext(PostsWrapper data) {
        boolean pullUpToRefresh = isPullUpToRefresh();
        if (pullUpToRefresh) {
            // mRecyclerAdapter.getItemCount() = 0
            // when configuration changes (like orientation changes)
            if (mRecyclerAdapter.getItemCount() != 0) {
                mRecyclerAdapter.hideFooterProgress();
            }
        }

        Posts posts = data.getPosts();
        List<Post> postList = posts.getPostList();
        // if user has logged out, has no permission to access this thread or this thread is invalid
        if (postList.isEmpty()) {
            consumeResult(data.getResult());
        } else {
            super.onNext(data);

            if (blacklistChanged){
                blacklistChanged = false;
                mRecyclerAdapter.setDataSet(postList);
                mRecyclerAdapter.notifyDataSetChanged();
            }else if (pullUpToRefresh) {
                final int oldItemCount = mRecyclerAdapter.getItemCount();
                // oldItemCount = 0 when configuration changes
                if (oldItemCount != 0 && mRecyclerAdapter.getItemId(oldItemCount - 1)
                        != Long.parseLong(postList.get(postList.size() - 1).getId())) {
                    // notify data set change if someone deleted their posts
                    mRecyclerAdapter.setDataSet(postList);
                    mRecyclerAdapter.notifyDataSetChanged();
                } else {
                    mRecyclerAdapter.setDataSet(postList);

                    int newItemCount = mRecyclerAdapter.getItemCount() - oldItemCount;
                    if (newItemCount > 0) {
                        mRecyclerAdapter.notifyItemRangeInserted(oldItemCount, newItemCount);
                    }
                }
            } else {
                mRecyclerAdapter.setDataSet(postList);
                mRecyclerAdapter.notifyDataSetChanged();

                String quotePostId = getArguments().getString(ARG_QUOTE_POST_ID);
                if (!TextUtils.isEmpty(quotePostId)) {
                    for (int i = 0, length = postList.size(); i < length; i++) {
                        if (quotePostId.equals(postList.get(i).getId())) {
                            // scroll to post post
                            mRecyclerView.scrollToPosition(i);
                            break;
                        }
                    }
                    // clear this argument after redirecting
                    getArguments().putString(ARG_QUOTE_POST_ID, null);
                }
            }

            Thread postListInfo = posts.getPostListInfo();
            // we have not title if we open a thread link in our app
            if (TextUtils.isEmpty(getActivity().getTitle())) {
                mPagerCallback.setThreadTitle(postListInfo.getTitle());
            }
            mPagerCallback.setTotalPageByPosts(postListInfo.getReplies() + 1);
            if (posts.getThreadAttachment() != null) {
                mPagerCallback.setupThreadAttachment(posts.getThreadAttachment());
            }
        }
    }

    @Override
    void onError(Throwable throwable) {
        if (isPullUpToRefresh()) {
            mRecyclerAdapter.hideFooterProgress();
        }

        //网络请求失败下依然刷新黑名单
        if (blacklistChanged){
            List<Object> dataSet = mRecyclerAdapter.getDataSet();
            List<Object> newData = new ArrayList<>();
            for (Object obj:dataSet){
                if (obj instanceof Post){
                    obj = Posts.getFilterPost((Post) obj);
                }
                if (obj != null){
                    newData.add(obj);
                }
            }
            blacklistChanged = false;
            mRecyclerAdapter.setDataSet(newData);
            mRecyclerAdapter.notifyDataSetChanged();
        }

        super.onError(throwable);
    }

    public interface PagerCallback {

        /**
         * Gets {@link android.support.v4.view.PagerAdapter#getCount()}.
         */
        int getTotalPages();

        /**
         * A callback to set actual total pages
         * which used for {@link android.support.v4.view.PagerAdapter}。
         */
        void setTotalPageByPosts(int threads);

        void setThreadTitle(CharSequence title);

        void setupThreadAttachment(Posts.ThreadAttachment threadAttachment);
    }
}
