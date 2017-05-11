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
import android.widget.TextView;

import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.Reply;
import io.rx_cache2.Source;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.api.model.collection.Posts;
import me.ykrank.s1next.data.api.model.wrapper.PostsWrapper;
import me.ykrank.s1next.data.db.ReadProgressDbWrapper;
import me.ykrank.s1next.data.db.dbmodel.ReadProgress;
import me.ykrank.s1next.data.event.PostSelectableChangeEvent;
import me.ykrank.s1next.data.event.QuickSidebarEnableChangeEvent;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.databinding.FragmentBaseWithQuickSideBarBinding;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.LooperUtil;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.adapter.PostListRecyclerViewAdapter;
import me.ykrank.s1next.view.internal.LoadingViewModelBindingDelegate;
import me.ykrank.s1next.view.internal.LoadingViewModelBindingDelegateQuickSidebarImpl;
import me.ykrank.s1next.view.internal.PagerScrollState;
import me.ykrank.s1next.viewmodel.LoadingViewModel;
import me.ykrank.s1next.widget.EventBus;

/**
 * A Fragment representing one of the pages of posts.
 * <p>
 * Activity or Fragment containing this must implement {@link PagerCallback}.
 */
public final class PostListPagerFragment extends BaseRecyclerViewFragment<PostsWrapper>
        implements OnQuickSideBarTouchListener {

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
    @Inject
    GeneralPreferencesManager mGeneralPreferencesManager;
    @Inject
    ObjectMapper objectMapper;

    private String mThreadId;
    private int mPageNum;
    /**
     * 之前记录的阅读进度
     */
    private ReadProgress readProgress;
    private PagerScrollState scrollState;
    private boolean blacklistChanged = false;

    private FragmentBaseWithQuickSideBarBinding binding;
    private RecyclerView mRecyclerView;
    private PostListRecyclerViewAdapter mRecyclerAdapter;
    private LinearLayoutManager mLayoutManager;
    private QuickSideBarView quickSideBarView;
    private TextView quickSideBarTipsView;
    private HashMap<String, Integer> letters = new HashMap<>();

    private PagerCallback mPagerCallback;

    private Disposable saveReadProgressDisposable;
    private Disposable blackListDisposable;
    private Disposable changeSeletableDisposable;
    private Disposable changeQuickSidebarEnableDisposable;

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

        quickSideBarView.setOnQuickSideBarTouchListener(this);

        changeSeletableDisposable = mEventBus.get()
                .ofType(PostSelectableChangeEvent.class)
                .subscribe(event -> {
                    mRecyclerAdapter.notifyDataSetChanged();
                }, super::onError);

        changeQuickSidebarEnableDisposable = mEventBus.get()
                .ofType(QuickSidebarEnableChangeEvent.class)
                .subscribe(event -> {
                    invalidateQuickSidebarVisible();
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
        RxJavaUtil.disposeIfNotNull(changeQuickSidebarEnableDisposable);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        RxJavaUtil.disposeIfNotNull(saveReadProgressDisposable);
        super.onDestroy();
    }

    @Override
    LoadingViewModelBindingDelegate getLoadingViewModelBindingDelegateImpl(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_with_quick_side_bar, container, false);
        binding.setQuickSidebarEnable(false);
        quickSideBarView = binding.quickSideBarView;
        quickSideBarTipsView = binding.quickSideBarViewTips;
        return new LoadingViewModelBindingDelegateQuickSidebarImpl(binding);
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
        if (scrollState == null) {
            scrollState = new PagerScrollState();
            scrollState.setState(PagerScrollState.BEFORE_SCROLL_POSITION);
        }
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

    @Override
    Observable<PostsWrapper> getSourceObservable(@LoadingViewModel.LoadingDef int loading) {
        return apiCacheProvider.getPostsWrapper(mS1Service.getPostsWrapper(mThreadId, mPageNum),
                new DynamicKeyGroup(mThreadId + "," + mPageNum, mUser.getKey()),
                new EvictDynamicKeyGroup(isForceLoading() || mPageNum >= mPagerCallback.getTotalPages()))
                .flatMap(o -> {
                    PostsWrapper wrapper = objectMapper.readValue(o.getData(), PostsWrapper.class);
                    if (o.getSource() != Source.CLOUD && wrapper.getData().getPostList().size() < Api.POSTS_PER_PAGE) {
                        return apiCacheProvider.getPostsWrapper(mS1Service.getPostsWrapper(mThreadId, mPageNum),
                                new DynamicKeyGroup(mThreadId + "," + mPageNum, mUser.getKey()), new EvictDynamicKeyGroup(true))
                                .map(Reply::getData)
                                .compose(RxJavaUtil.jsonTransformer(PostsWrapper.class));
                    }
                    return Observable.just(wrapper);
                });
    }

    @Override
    void onNext(PostsWrapper data) {
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
            Thread postListInfo = posts.getPostListInfo();
            if (postListInfo != null) {
                mRecyclerAdapter.setThreadInfo(postListInfo);
            }

            mRecyclerAdapter.diffNewDataSet(postList, true);
            if (blacklistChanged) {
                blacklistChanged = false;
            } else if (pullUpToRefresh) {

            } else if (readProgress != null && scrollState != null && scrollState.getState() == PagerScrollState.BEFORE_SCROLL_POSITION) {
                mRecyclerView.scrollToPosition(readProgress.getPosition());
                readProgress = null;
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

            if (postListInfo != null) {
                // we have not title if we open a thread link in our app
                mPagerCallback.setThreadTitle(postListInfo.getTitle());

                mPagerCallback.setTotalPageByPosts(postListInfo.getReliesCount() + 1);
                if (posts.getThreadAttachment() != null) {
                    mPagerCallback.setupThreadAttachment(posts.getThreadAttachment());
                }
            }

            initQuickSidebar(mPageNum, postList.size());
        }
    }

    @Override
    void onError(Throwable throwable) {
        //网络请求失败下依然刷新黑名单
        if (blacklistChanged) {
            RxJavaUtil.disposeIfNotNull(blackListDisposable);
            blackListDisposable = Single.just(mRecyclerAdapter.getDataSet())
                    .map(dataSet -> {
                        List<Object> newData = new ArrayList<>();
                        for (Object obj : dataSet) {
                            if (obj instanceof Post) {
                                obj = Posts.filterPost((Post) obj);
                                if (obj != null) {
                                    newData.add(obj);
                                }
                            }
                        }
                        return newData;
                    })
                    .compose(RxJavaUtil.iOSingleTransformer())
                    .subscribe(newData -> {
                        blacklistChanged = false;
                        mRecyclerAdapter.diffNewDataSet(newData, false);
                    }, L::report);
        } else if (isPullUpToRefresh()) {
            mRecyclerAdapter.hideFooterProgress();
        }

        super.onError(throwable);
    }

    boolean invalidateQuickSidebarVisible() {
        boolean enable = mGeneralPreferencesManager.isQuickSideBarEnable();
        binding.setQuickSidebarEnable(enable);
        return enable;
    }

    private void initQuickSidebar(int page, int postSize) {
        invalidateQuickSidebarVisible();
        List<String> customLetters = new ArrayList<>();
        for (int i = 0; i < postSize; i++) {
            //1-10, then interval 2
            if (i >= 10 && i % 2 == 0) {
                continue;
            }
            String letter = String.valueOf(i + 1 + 30 * (page - 1));
            customLetters.add(letter);
            letters.put(letter, i);
        }
        quickSideBarView.setLetters(customLetters);
    }

    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter);
        //有此key则获取位置并滚动到该位置
        if (letters.containsKey(letter)) {
            mLayoutManager.scrollToPositionWithOffset(letters.get(letter), 0);
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
//        quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
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
