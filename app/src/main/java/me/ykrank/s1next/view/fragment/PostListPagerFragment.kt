package me.ykrank.s1next.view.fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bigkoo.quicksidebar.QuickSideBarView
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.ui.internal.LoadingViewModelBindingDelegate
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.LooperUtil
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.github.ykrank.androidtools.widget.RxBus
import com.github.ykrank.androidtools.widget.recycleview.StartSnapLinearLayoutManager
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.rx_cache2.DynamicKeyGroup
import io.rx_cache2.EvictDynamicKeyGroup
import io.rx_cache2.Reply
import io.rx_cache2.Source
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.ApiUtil
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Rate
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.api.model.collection.Posts
import me.ykrank.s1next.data.api.model.wrapper.PostsWrapper
import me.ykrank.s1next.data.db.ReadProgressDbWrapper
import me.ykrank.s1next.data.db.dbmodel.ReadProgress
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.databinding.FragmentBaseWithQuickSideBarBinding
import me.ykrank.s1next.util.JsonUtil
import me.ykrank.s1next.view.adapter.PostListRecyclerViewAdapter
import me.ykrank.s1next.view.event.BlackListChangeEvent
import me.ykrank.s1next.view.event.PostSelectableChangeEvent
import me.ykrank.s1next.view.event.QuickSidebarEnableChangeEvent
import me.ykrank.s1next.view.fragment.PostListPagerFragment.PagerCallback
import me.ykrank.s1next.view.internal.LoadingViewModelBindingDelegateQuickSidebarImpl
import me.ykrank.s1next.view.internal.PagerScrollState
import java.util.*
import javax.inject.Inject

/**
 * A Fragment representing one of the pages of posts.
 *
 *
 * Activity or Fragment containing this must implement [PagerCallback].
 */
class PostListPagerFragment : BaseRecyclerViewFragment<PostsWrapper>(), OnQuickSideBarTouchListener {

    @Inject
    internal lateinit var mRxBus: RxBus
    @Inject
    internal lateinit var mGeneralPreferencesManager: GeneralPreferencesManager
    @Inject
    internal lateinit var objectMapper: ObjectMapper

    private var mThreadId: String? = null
    private var mPageNum: Int = 0
    /**
     * Only see this author, or all if null
     */
    private var mAuthorId: String? = null
    /**
     * 之前记录的阅读进度
     */
    private var readProgress: ReadProgress? = null
    private var scrollState: PagerScrollState? = null
    private var blacklistChanged = false

    private lateinit var binding: FragmentBaseWithQuickSideBarBinding
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: PostListRecyclerViewAdapter
    private lateinit var mLayoutManager: StartSnapLinearLayoutManager
    private lateinit var quickSideBarView: QuickSideBarView
    private lateinit var quickSideBarTipsView: TextView
    private val letters = HashMap<String, Int>()

    private var mPagerCallback: PagerCallback? = null

    private var refreshAfterBlacklistChangeDisposable: Disposable? = null

    private val rateMap = hashMapOf<Int, List<Rate>>()
    private var rateStamp: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments!!
        mThreadId = bundle.getString(ARG_THREAD_ID)
        mPageNum = bundle.getInt(ARG_PAGE_NUM)
        mAuthorId = bundle.getString(ARG_AUTHOR_ID)
        if (readProgress == null) {
            readProgress = bundle.getParcelable(ARG_READ_PROGRESS)
            scrollState = bundle.getParcelable(ARG_PAGER_SCROLL_STATE)
        }
        L.leaveMsg("PostListPagerFragment##ThreadId:$mThreadId,PageNum:$mPageNum")

        mRecyclerView = recyclerView
        mLayoutManager = StartSnapLinearLayoutManager(activity!!)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerAdapter = PostListRecyclerViewAdapter(this, context!!)
        mRecyclerView.adapter = mRecyclerAdapter

        // add pull up to refresh to RecyclerView
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (!isPullUpToRefresh
                        && mPageNum == mPagerCallback?.getTotalPages()
                        && !isLoading
                        && mRecyclerAdapter.itemCount != 0
                        && !mRecyclerView.canScrollVertically(1)) {
                    startPullToRefresh()
                }
            }
        })

        quickSideBarView.setOnQuickSideBarTouchListener(this)

        mRxBus.get()
                .ofType(PostSelectableChangeEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.DESTROY_VIEW))
                .subscribe({ mRecyclerAdapter.notifyDataSetChanged() }, { super.onError(it) })

        mRxBus.get()
                .ofType(QuickSidebarEnableChangeEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.DESTROY_VIEW))
                .subscribe({ invalidateQuickSidebarVisible() }, { super.onError(it) })

        mRxBus.get()
                .ofType(BlackListChangeEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.DESTROY_VIEW))
                .subscribe { startBlackListRefresh() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPagerCallback = parentFragment as PagerCallback
    }

    override fun onDestroy() {
        RxJavaUtil.disposeIfNotNull(refreshAfterBlacklistChangeDisposable)
        mPagerCallback = null
        super.onDestroy()
    }

    override fun getLoadingViewModelBindingDelegateImpl(inflater: LayoutInflater, container: ViewGroup?): LoadingViewModelBindingDelegate {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_with_quick_side_bar, container, false)
        binding.quickSidebarEnable = false
        quickSideBarView = binding.quickSideBarView
        quickSideBarTipsView = binding.quickSideBarViewTips
        return LoadingViewModelBindingDelegateQuickSidebarImpl(binding)
    }

    override fun startPullToRefresh() {
        if (isPullUpToRefreshValid) {
            mRecyclerAdapter.showFooterProgress()
            super.startPullToRefresh()
        }
    }

    /**
     * 黑名单更改后刷新当前帖子列表
     */
    internal fun startBlackListRefresh() {
        blacklistChanged = true
        startPullToRefresh()
    }

    internal fun loadReadProgressInRecycleView(readProgress: ReadProgress, smooth: Boolean) {
        this.readProgress = readProgress
        if (scrollState == null) {
            scrollState = PagerScrollState()
            scrollState!!.state = PagerScrollState.BEFORE_SCROLL_POSITION
        }
        if (!isLoading) {
            var position = readProgress.position
            var offset = readProgress.offset
            if (position <= 0) {
                //if position invalid or first, offset should below zero
                position = 0
                if (offset > 0) {
                    offset = 0
                }
            }
            val totalItemCount = mRecyclerAdapter.itemCount
            if (totalItemCount <= position) {
                position = totalItemCount - 1
            }
            if (smooth) {
                mLayoutManager.smoothScrollToPosition(position, offset)
            } else {
                mLayoutManager.scrollToPositionWithOffset(position, offset)
            }
        }
    }

    /**
     * 保存当前阅读进度
     */
    internal fun saveReadProgress() {
        val readProgress = curReadProgress
        if (readProgress != null) {
            Single.fromCallable {
                LooperUtil.enforceOnWorkThread()
                val dbWrapper = ReadProgressDbWrapper.getInstance()
                dbWrapper.saveReadProgress(readProgress)
                true
            }.compose(RxJavaUtil.iOSingleTransformer<Boolean>())
                    .to(AndroidRxDispose.withSingle<Boolean>(this, FragmentEvent.DESTROY))
                    .subscribe({ b ->
                        LooperUtil.enforceOnMainThread()
                        showShortText(R.string.save_read_progress_success)
                    }, { L.report(it) })
        }
    }

    internal val curReadProgress: ReadProgress?
        get() {
            if (isLoading) {
                return null
            }
            val itemPosition = findNowItemPosition()
            return ReadProgress(Integer.valueOf(mThreadId), mPageNum, itemPosition.first!!, itemPosition.second!!)
        }

    /**
     * 现在Item的位置

     * @return
     */
    private fun findNowItemPosition(): Pair<Int, Int> {
        var itemPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition()
        if (itemPosition == RecyclerView.NO_POSITION) {
            itemPosition = mLayoutManager.findFirstVisibleItemPosition()
        }
        var offset = 0
        val view = mLayoutManager.findViewByPosition(itemPosition)
        if (view != null) {
            offset = view.top
        }
        return Pair(itemPosition, offset)
    }

    override fun getSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<PostsWrapper> {
        val source: Single<PostsWrapper> = if (mDownloadPrefManager.netCacheEnable) {
            apiCacheObservable(isForceLoading || mPageNum >= mPagerCallback?.getTotalPages() ?: 0)
                    .flatMap {
                        val wrapper = objectMapper.readValue(it.data, PostsWrapper::class.java)
                        if (it.source != Source.CLOUD && wrapper.data.postList.size < Api.POSTS_PER_PAGE) {
                            return@flatMap apiCacheObservable(true)
                                    .map<String> { it.data }
                                    .compose(JsonUtil.jsonSingleTransformer(PostsWrapper::class.java))
                        }
                        Single.just(wrapper)
                    }
        } else {
            apiObservable().compose(JsonUtil.jsonSingleTransformer(PostsWrapper::class.java))
        }
        return source.flatMap { o ->
            if (o.data != null) {
                val postList = o.data.postList
                if (postList.isNotEmpty()) {
                    val post = postList[0]
                    if (post.isTrade) {
                        post.extraHtml = ""
                        return@flatMap mS1Service.getTradePostInfo(mThreadId, post.id + 1)
                                .map<PostsWrapper> { html ->
                                    post.extraHtml = ApiUtil.replaceAjaxHeader(html)
                                    return@map o
                                }
                    }
                }
            }
            Single.just(o)
        }
    }

    private fun apiObservable(): Single<String> {
        return mS1Service.getPostsWrapper(mThreadId, mPageNum, mAuthorId)
    }

    private fun apiCacheObservable(evict: Boolean): Single<Reply<String>> {
        return apiCacheProvider.getPostsWrapper(apiObservable(),
                DynamicKeyGroup("$mThreadId,$mPageNum,$mAuthorId", mUser.key),
                EvictDynamicKeyGroup(evict))
    }

    override fun onNext(data: PostsWrapper) {
        val pullUpToRefresh = isPullUpToRefresh
        var postList: List<Post>? = null

        val posts = data.data
        if (posts != null) {
            postList = posts.postList
        }

        // if user has logged out, has no permission to access this thread or this thread is invalid
        if (postList == null || postList.isEmpty()) {
            if (pullUpToRefresh) {
                // mRecyclerAdapter.getItemCount() = 0
                // when configuration changes (like orientation changes)
                if (mRecyclerAdapter.itemCount != 0) {
                    mRecyclerAdapter.hideFooterProgress()
                }
            }
            consumeResult(data.result)
        } else {
            super.onNext(data)

            //Set old rates to data
            postList.forEach {
                val rates = rateMap[it.id]
                if (rates != null) {
                    it.rates = rates
                }
            }
            if (!pullUpToRefresh) {
                //Do not load rates if pull up refresh
                loadRates(postList)
            }

            //Thread info must not null, or exception
            val postListInfo = posts.postListInfo as Thread
            mRecyclerAdapter.setThreadInfo(postListInfo)

            posts.vote?.let {
                mRecyclerAdapter.setVoteInfo(it)
            }

            mRecyclerAdapter.diffNewDataSet(postList, true)
            if (blacklistChanged) {
                blacklistChanged = false
            } else if (pullUpToRefresh) {

            } else if (readProgress != null && scrollState?.state == PagerScrollState.BEFORE_SCROLL_POSITION) {
                mLayoutManager.scrollToPositionWithOffset(readProgress!!.position, readProgress!!.offset)
                readProgress = null
                scrollState!!.state = PagerScrollState.FREE
            } else {
                val quotePostId = arguments?.getString(ARG_QUOTE_POST_ID)
                if (!TextUtils.isEmpty(quotePostId)) {
                    var i = 0
                    val length = postList.size
                    while (i < length) {
                        if (Integer.parseInt(quotePostId) == postList[i].id) {
                            // scroll to post post
                            mLayoutManager.scrollToPositionWithOffset(i, 0)
                            break
                        }
                        i++
                    }
                    // clear this argument after redirecting
                    arguments?.putString(ARG_QUOTE_POST_ID, null)
                }
            }

            mPagerCallback?.threadInfo = postListInfo
            posts.threadAttachment?.let {
                mPagerCallback?.setupThreadAttachment(it)
            }

            initQuickSidebar(mPageNum, postList.size)
        }
    }

    override fun onError(throwable: Throwable) {
        //网络请求失败下依然刷新黑名单
        if (blacklistChanged) {
            blacklistChanged = false
            RxJavaUtil.disposeIfNotNull(refreshAfterBlacklistChangeDisposable)
            val dataSet = mRecyclerAdapter.dataSet
            refreshAfterBlacklistChangeDisposable = Single.just(dataSet)
                    .map { filterPostAfterBlacklistChanged(it) }
                    .compose(RxJavaUtil.iOSingleTransformer())
                    .subscribe({ mRecyclerAdapter.diffNewDataSet(it, false) }, { L.report(it) })
        } else if (isPullUpToRefresh) {
            mRecyclerAdapter.hideFooterProgress()
        }

        super.onError(throwable)
    }

    internal fun invalidateQuickSidebarVisible(): Boolean {
        val enable = mGeneralPreferencesManager.isQuickSideBarEnable
        binding.quickSidebarEnable = enable
        return enable
    }

    private fun loadRates(posts: List<Post>) {
        val time = SystemClock.elapsedRealtime()
        if (time - rateStamp < 10_000) {
            //Do not load too frequently
            return
        }
        rateStamp = time

        Observable.fromIterable(posts.filter { it.rates != null }.distinctBy { it.id })
                .flatMap {
                    val pid = it.id
                    mS1Service.getRates(mThreadId, pid.toString())
                            .map { Pair(pid, Rate.fromHtml(it)) }
                            .toObservable()
                }
                .compose(RxJavaUtil.iOTransformer())
                .subscribe({
                    val pid = it.first
                    val rates = it.second
                    if (pid != null && rates != null) {
                        rateMap[pid] = rates
                        mRecyclerAdapter.dataSet.filterIsInstance(Post::class.java)
                                .forEachIndexed { index, post ->
                                    if (post.id == pid) {
                                        post.rates = rates
                                        mRecyclerAdapter.notifyItemChanged(index)
                                    }
                                }
                    }
                }, L::report)
    }

    private fun initQuickSidebar(page: Int, postSize: Int) {
        invalidateQuickSidebarVisible()
        val customLetters = ArrayList<String>()
        for (i in 0 until postSize) {
            //1-10, then interval 2
            if (i >= 10 && i % 2 == 0) {
                continue
            }
            val letter = (i + 1 + 30 * (page - 1)).toString()
            customLetters.add(letter)
            letters[letter] = i
        }
        quickSideBarView.letters = customLetters
    }

    override fun onLetterChanged(letter: String, position: Int, y: Float) {
        quickSideBarTipsView.text = letter
        //有此key则获取位置并滚动到该位置
        if (letters.containsKey(letter)) {
            mLayoutManager.scrollToPositionWithOffset(letters[letter] ?: 0, 0)
        }
    }

    override fun onLetterTouching(touching: Boolean) {
        //        quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
    }

    interface PagerCallback {

        /**
         * Gets [android.support.v4.view.PagerAdapter.getCount].
         */
        fun getTotalPages(): Int

        fun setupThreadAttachment(threadAttachment: Posts.ThreadAttachment)

        var threadInfo: Thread?
    }

    companion object {

        private const val ARG_THREAD_ID = "thread_id"
        private const val ARG_PAGE_NUM = "page_num"
        private const val ARG_AUTHOR_ID = "author_id"
        private const val ARG_READ_PROGRESS = "read_progress"
        private const val ARG_PAGER_SCROLL_STATE = "pager_scroll_state"

        /**
         * Used for post post redirect.
         */
        private const val ARG_QUOTE_POST_ID = "quote_post_id"

        fun newInstance(threadId: String, pageNum: Int): PostListPagerFragment {
            return newInstance(threadId, pageNum, null, null, null, null)
        }

        fun newInstance(threadId: String, pageNum: Int, progress: ReadProgress, scrollState: PagerScrollState): PostListPagerFragment {
            return newInstance(threadId, pageNum, null, null, progress, scrollState)
        }

        fun newInstance(threadId: String, pageNum: Int, postId: String): PostListPagerFragment {
            return newInstance(threadId, pageNum, null, postId, null, null)
        }

        fun newInstance(threadId: String, pageNum: Int, authorId: String?,
                        postId: String?, progress: ReadProgress?, scrollState: PagerScrollState?): PostListPagerFragment {
            val fragment = PostListPagerFragment()
            val bundle = Bundle()
            bundle.putString(ARG_THREAD_ID, threadId)
            if (!TextUtils.isEmpty(postId)) {
                bundle.putString(ARG_QUOTE_POST_ID, postId)
            }
            bundle.putString(ARG_AUTHOR_ID, authorId)
            bundle.putInt(ARG_PAGE_NUM, pageNum)
            bundle.putParcelable(ARG_READ_PROGRESS, progress)
            bundle.putParcelable(ARG_PAGER_SCROLL_STATE, scrollState)
            fragment.arguments = bundle

            return fragment
        }

        internal fun saveReadProgressBack(readProgress: ReadProgress) {
            java.lang.Thread {
                val dbWrapper = ReadProgressDbWrapper.getInstance()
                dbWrapper.saveReadProgress(readProgress)
            }.start()
        }

        private fun filterPostAfterBlacklistChanged(dataSet: List<Any>): List<Any> {
            LooperUtil.enforceOnWorkThread()
            return dataSet.filterIsInstance<Post>()
                    .mapNotNull { Posts.filterPost(it) }
        }
    }
}
