package me.ykrank.s1next.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bugsnag.android.Bugsnag;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.api.model.collection.Posts;
import me.ykrank.s1next.data.api.model.wrapper.PostsWrapper;
import me.ykrank.s1next.data.db.ReadProgressDbWrapper;
import me.ykrank.s1next.data.db.dbmodel.ReadProgress;
import me.ykrank.s1next.data.pref.ReadProgressPreferencesManager;
import me.ykrank.s1next.databinding.FragmentBaseCardViewContainerBinding;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.adapter.PostListRecyclerViewAdapter;
import me.ykrank.s1next.view.internal.LoadingViewModelBindingDelegate;
import me.ykrank.s1next.view.internal.LoadingViewModelBindingDelegateBaseCardViewContainerImpl;
import rx.Observable;
import rx.Subscription;

/**
 * A Fragment representing one of the pages of posts.
 * <p>
 * Activity or Fragment containing this must implement {@link PagerCallback}.
 */
public final class PostListPagerFragment extends BaseRecyclerViewFragment<PostsWrapper> {

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_PAGE_NUM = "page_num";
    private static final String ARG_READ_PROGRESS = "read_progress";

    /**
     * Used for post post redirect.
     */
    private static final String ARG_QUOTE_POST_ID = "quote_post_id";

    @Inject
    ReadProgressPreferencesManager mReadProgressPrefManager;

    private String mThreadId;
    private int mPageNum;
    /**
     * 之前记录的阅读进度
     */
    private ReadProgress readProgress;
    private boolean blacklistChanged = false;

    private RecyclerView mRecyclerView;
    private PostListRecyclerViewAdapter mRecyclerAdapter;
    private LinearLayoutManager mLayoutManager;

    private PagerCallback mPagerCallback;

    private Subscription saveReadProgressSubscription;

    public static PostListPagerFragment newInstance(String threadId, int pageNum) {
        return newInstance(threadId, pageNum, null, null);
    }

    public static PostListPagerFragment newInstance(String threadId, int pageNum, ReadProgress progress) {
        return newInstance(threadId, pageNum, null, progress);
    }

    public static PostListPagerFragment newInstance(String threadId, int pageNum, String postId) {
        return newInstance(threadId, pageNum, postId, null);
    }

    private static PostListPagerFragment newInstance(String threadId, int pageNum, String postId, ReadProgress progress) {
        PostListPagerFragment fragment = new PostListPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_THREAD_ID, threadId);
        if (!TextUtils.isEmpty(postId)) {
            bundle.putString(ARG_QUOTE_POST_ID, postId);
        }
        bundle.putInt(ARG_PAGE_NUM, pageNum);
        bundle.putParcelable(ARG_READ_PROGRESS, progress);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.getAppComponent(getContext()).inject(this);

        mThreadId = getArguments().getString(ARG_THREAD_ID);
        mPageNum = getArguments().getInt(ARG_PAGE_NUM);
        if (readProgress == null){
            readProgress = getArguments().getParcelable(ARG_READ_PROGRESS);
        }
        Bugsnag.leaveBreadcrumb("PostListPagerFragment##ThreadId:"+mThreadId+",PageNum:"+mPageNum);

        mRecyclerView = getRecyclerView();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
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
    public void onDestroy() {
        if (mReadProgressPrefManager.isSaveAuto())
            saveReadProgressBack(mThreadId, mPageNum, findMidItemPosition());
        RxJavaUtil.unsubscribeIfNotNull(saveReadProgressSubscription);
        super.onDestroy();
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

    /**
     * 黑名单更改后刷新当前帖子列表
     */
    void startBlackListRefresh() {
        blacklistChanged = true;
        startPullToRefresh();
    }

    void setReadProgress(ReadProgress readProgress, boolean smooth){
        this.readProgress = readProgress;
        if (!isLoading()){
            if (smooth) {
                mRecyclerView.smoothScrollToPosition(readProgress.position);
            }else {
                mRecyclerView.scrollToPosition(readProgress.position);
            }
        }
    }

    /**
     * 保存当前阅读进度
     */
    void saveReadProgress() {
        saveReadProgressSubscription = RxJavaUtil.workWithUiThread(() -> {
//            LooperUtil.enforceOnWorkThread();
            int visiblePosition = findMidItemPosition();
            ReadProgress readProgress = new ReadProgress(mThreadId, mPageNum, visiblePosition);
            ReadProgressDbWrapper dbWrapper = ReadProgressDbWrapper.getInstance();
            dbWrapper.saveReadProgress(readProgress);
        }, () -> {
//            LooperUtil.enforceOnMainThread();
            showShortText(R.string.save_read_progress_success);
        });
    }

    static void saveReadProgressBack(String threadId, int page, int position){
        new java.lang.Thread(()->{
            ReadProgress readProgress = new ReadProgress(threadId, page, position);
            ReadProgressDbWrapper dbWrapper = ReadProgressDbWrapper.getInstance();
            dbWrapper.saveReadProgress(readProgress);
        }).start();
    }

    /**
     * 现在Item的位置
     *
     * @return
     */
    private int findMidItemPosition() {
        return (mLayoutManager.findFirstCompletelyVisibleItemPosition()
                + mLayoutManager.findLastCompletelyVisibleItemPosition()) / 2;
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

            mRecyclerAdapter.diffNewDataSet(postList, true);
            if (blacklistChanged) {
                blacklistChanged = false;
            } else if (pullUpToRefresh) {

            } else if (readProgress != null && readProgress.scrollState == ReadProgress.BEFORE_SCROLL_POSITION) {
                mRecyclerView.scrollToPosition(readProgress.position);
                readProgress.scrollState = ReadProgress.FREE;
            } else {
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
        if (blacklistChanged) {
            List<Object> dataSet = mRecyclerAdapter.getDataSet();
            List<Object> newData = new ArrayList<>();
            for (Object obj : dataSet) {
                if (obj instanceof Post) {
                    obj = Posts.getFilterPost((Post) obj);
                }
                if (obj != null) {
                    newData.add(obj);
                }
            }
            blacklistChanged = false;
            mRecyclerAdapter.diffNewDataSet(newData, false);
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
