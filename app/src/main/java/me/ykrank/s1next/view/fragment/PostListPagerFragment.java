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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.api.model.collection.Posts;
import me.ykrank.s1next.data.api.model.wrapper.BaseResultWrapper;
import me.ykrank.s1next.data.db.ReadProgressDbWrapper;
import me.ykrank.s1next.data.db.dbmodel.ReadProgress;
import me.ykrank.s1next.data.event.PostSelectableChangeEvent;
import me.ykrank.s1next.databinding.FragmentBaseCardViewContainerBinding;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.LooperUtil;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.adapter.PostListRecyclerViewAdapter;
import me.ykrank.s1next.view.internal.LoadingViewModelBindingDelegate;
import me.ykrank.s1next.view.internal.LoadingViewModelBindingDelegateBaseCardViewContainerImpl;
import me.ykrank.s1next.view.internal.PagerScrollState;
import me.ykrank.s1next.widget.EventBus;

/**
 * A Fragment representing one of the pages of posts.
 * <p>
 * Activity or Fragment containing this must implement {@link PagerCallback}.
 */
public final class PostListPagerFragment extends BaseRecyclerViewFragment<BaseResultWrapper<Posts>> {

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_PAGE_NUM = "page_num";
    private static final String ARG_READ_PROGRESS = "read_progress";
    private static final String ARG_PAGER_SCROLL_STATE = "pager_scroll_state";

    /**
     * Used for post post redirect.
     */
    private static final String ARG_QUOTE_POST_ID = "quote_post_id";

    @Inject
    EventBus mEventBus;

    private String mThreadId;
    private int mPageNum;
    /**
     * 之前记录的阅读进度
     */
    private ReadProgress readProgress;
    private PagerScrollState scrollState;
    private boolean blacklistChanged = false;

    private RecyclerView mRecyclerView;
    private PostListRecyclerViewAdapter mRecyclerAdapter;
    private LinearLayoutManager mLayoutManager;

    private PagerCallback mPagerCallback;

    private Disposable saveReadProgressDisposable;
    private Disposable changeSeletableDisposable;

    public static PostListPagerFragment newInstance(String threadId, int pageNum) {
        return newInstance(threadId, pageNum, null, null, null);
    }

    public static PostListPagerFragment newInstance(String threadId, int pageNum, ReadProgress progress, PagerScrollState scrollState) {
        return newInstance(threadId, pageNum, null, progress, scrollState);
    }

    public static PostListPagerFragment newInstance(String threadId, int pageNum, String postId) {
        return newInstance(threadId, pageNum, postId, null, null);
    }

    private static PostListPagerFragment newInstance(String threadId, int pageNum, String postId, ReadProgress progress, PagerScrollState scrollState) {
        PostListPagerFragment fragment = new PostListPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_THREAD_ID, threadId);
        if (!TextUtils.isEmpty(postId)) {
            bundle.putString(ARG_QUOTE_POST_ID, postId);
        }
        bundle.putInt(ARG_PAGE_NUM, pageNum);
        bundle.putParcelable(ARG_READ_PROGRESS, progress);
        bundle.putParcelable(ARG_PAGER_SCROLL_STATE, scrollState);
        fragment.setArguments(bundle);

        return fragment;
    }

    static void saveReadProgressBack(ReadProgress readProgress) {
        new java.lang.Thread(() -> {
            ReadProgressDbWrapper dbWrapper = ReadProgressDbWrapper.getInstance();
            dbWrapper.saveReadProgress(readProgress);
        }).start();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onViewCreated(view, savedInstanceState);

        mThreadId = getArguments().getString(ARG_THREAD_ID);
        mPageNum = getArguments().getInt(ARG_PAGE_NUM);
        if (readProgress == null) {
            readProgress = getArguments().getParcelable(ARG_READ_PROGRESS);
            scrollState = getArguments().getParcelable(ARG_PAGER_SCROLL_STATE);
        }
        L.leaveMsg("PostListPagerFragment##ThreadId:" + mThreadId + ",PageNum:" + mPageNum);

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

        changeSeletableDisposable = mEventBus.get()
                .ofType(PostSelectableChangeEvent.class)
                .subscribe(event -> {
                    mRecyclerAdapter.notifyDataSetChanged();
                }, super::onError);
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
    public void onDestroyView() {
        RxJavaUtil.disposeIfNotNull(changeSeletableDisposable);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        RxJavaUtil.disposeIfNotNull(saveReadProgressDisposable);
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

    void setReadProgress(ReadProgress readProgress, boolean smooth) {
        this.readProgress = readProgress;
        if (!isLoading()) {
            if (smooth) {
                mRecyclerView.smoothScrollToPosition(readProgress.getPosition());
            } else {
                mRecyclerView.scrollToPosition(readProgress.getPosition());
            }
        }
    }

    /**
     * 保存当前阅读进度
     */
    void saveReadProgress() {
        saveReadProgressDisposable = RxJavaUtil.workWithUiThread(() -> {
            LooperUtil.enforceOnWorkThread();
            ReadProgressDbWrapper dbWrapper = ReadProgressDbWrapper.getInstance();
            dbWrapper.saveReadProgress(getCurReadProgress());
        }, () -> {
            LooperUtil.enforceOnMainThread();
            showShortText(R.string.save_read_progress_success);
        });
    }

    ReadProgress getCurReadProgress() {
        return new ReadProgress(Integer.valueOf(mThreadId), mPageNum, findMidItemPosition());
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

    void notifyDataSetChanged() {
        if (mRecyclerAdapter != null) {
            getRecyclerView().setAdapter(mRecyclerAdapter);
        }
    }

    @Override
    Observable<BaseResultWrapper<Posts>> getSourceObservable() {
        return mS1Service.getPostsWrapper(mThreadId, mPageNum);
    }

    @Override
    void onNext(BaseResultWrapper<Posts> data) {
        boolean pullUpToRefresh = isPullUpToRefresh();
        List<Post> postList = null;

        Posts posts = data.getData();
        if (posts != null) {
            postList = posts.getPostList();
        }

        // if user has logged out, has no permission to access this thread or this thread is invalid
        if (postList == null || postList.isEmpty()) {
            if (pullUpToRefresh) {
                // mRecyclerAdapter.getItemCount() = 0
                // when configuration changes (like orientation changes)
                if (mRecyclerAdapter.getItemCount() != 0) {
                    mRecyclerAdapter.hideFooterProgress();
                }
            }
            consumeResult(data.getResult());
        } else {
            super.onNext(data);

            mRecyclerAdapter.diffNewDataSet(postList, true);
            if (blacklistChanged) {
                blacklistChanged = false;
            } else if (pullUpToRefresh) {

            } else if (readProgress != null && scrollState.getState() == PagerScrollState.BEFORE_SCROLL_POSITION) {
                mRecyclerView.scrollToPosition(readProgress.getPosition());
                scrollState.setState(PagerScrollState.FREE);
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
            mPagerCallback.setTotalPageByPosts(postListInfo.getReliesCount() + 1);
            if (posts.getThreadAttachment() != null) {
                mPagerCallback.setupThreadAttachment(posts.getThreadAttachment());
            }
        }
    }

    @Override
    void onError(Throwable throwable) {
        //网络请求失败下依然刷新黑名单
        if (blacklistChanged) {
            // FIXME: 2017/1/25 should not work on ui thread
            List<Object> dataSet = mRecyclerAdapter.getDataSet();
            List<Object> newData = new ArrayList<>();
            for (Object obj : dataSet) {
                if (obj instanceof Post) {
                    obj = Posts.filterPost((Post) obj);
                    if (obj != null) {
                        newData.add(obj);
                    }
                }
            }
            blacklistChanged = false;
            mRecyclerAdapter.diffNewDataSet(newData, false);
        } else if (isPullUpToRefresh()) {
            mRecyclerAdapter.hideFooterProgress();
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
