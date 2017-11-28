package me.ykrank.s1next.view.fragment

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
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
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.app.AppApiUtil
import me.ykrank.s1next.data.api.app.AppService
import me.ykrank.s1next.data.api.app.model.AppDataWrapper
import me.ykrank.s1next.data.api.app.model.AppPost
import me.ykrank.s1next.data.api.app.model.AppPostsWrapper
import me.ykrank.s1next.data.api.app.model.AppThread
import me.ykrank.s1next.data.db.ReadProgressDbWrapper
import me.ykrank.s1next.data.db.dbmodel.ReadProgress
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.databinding.FragmentBaseWithQuickSideBarBinding
import me.ykrank.s1next.util.JsonUtil
import me.ykrank.s1next.view.adapter.AppPostListRecyclerViewAdapter
import me.ykrank.s1next.view.event.*
import me.ykrank.s1next.view.fragment.AppPostListPagerFragment.PagerCallback
import me.ykrank.s1next.view.internal.LoadingViewModelBindingDelegateQuickSidebarImpl
import me.ykrank.s1next.view.internal.PagerScrollState
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * A Fragment representing one of the pages of posts.
 *
 *
 * Activity or Fragment containing this must implement [PagerCallback].
 */
class AppPostListPagerFragment : BaseRecyclerViewFragment<AppPostsWrapper>(), OnQuickSideBarTouchListener {

    @Inject
    internal lateinit var mRxBus: RxBus
    @Inject
    internal lateinit var mGeneralPreferencesManager: GeneralPreferencesManager
    @Inject
    internal lateinit var objectMapper: ObjectMapper
    @Inject
    internal lateinit var appService: AppService

    private var mThreadId: String? = null
    private var mPageNum: Int = 0
    /**
     * 之前记录的阅读进度
     */
    private var readProgress: ReadProgress? = null
    private var scrollState: PagerScrollState? = null
    private var blacklistChanged = false

    private lateinit var binding: FragmentBaseWithQuickSideBarBinding
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: AppPostListRecyclerViewAdapter
    private lateinit var mLayoutManager: StartSnapLinearLayoutManager
    private lateinit var quickSideBarView: QuickSideBarView
    private lateinit var quickSideBarTipsView: TextView
    private val letters = HashMap<String, Int>()

    private var mPagerCallback: PagerCallback? = null

    private var refreshAfterBlacklistChangeDisposable: Disposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)

        mThreadId = arguments.getString(ARG_THREAD_ID)
        mPageNum = arguments.getInt(ARG_PAGE_NUM)
        if (readProgress == null) {
            readProgress = arguments.getParcelable<ReadProgress>(ARG_READ_PROGRESS)
            scrollState = arguments.getParcelable<PagerScrollState>(ARG_PAGER_SCROLL_STATE)
        }
        L.leaveMsg("AppPostListPagerFragment##ThreadId:$mThreadId,PageNum:$mPageNum")

        mRecyclerView = recyclerView
        mLayoutManager = StartSnapLinearLayoutManager(activity)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerAdapter = AppPostListRecyclerViewAdapter(activity)
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
                .filter { it is AppLoginEvent || it is LoginEvent }
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.DESTROY_VIEW))
                .subscribe { startSwipeRefresh() }

        mRxBus.get()
                .ofType(BlackListChangeEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.DESTROY_VIEW))
                .subscribe { startBlackListRefresh() }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        mPagerCallback = context as PagerCallback
    }

    override fun onDetach() {
        super.onDetach()

        mPagerCallback = null
    }

    override fun onDestroy() {
        RxJavaUtil.disposeIfNotNull(refreshAfterBlacklistChangeDisposable)
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

    override fun getSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<AppPostsWrapper> {
        var postObservable = appService.getPostsWrapper(mUser.appSecureToken, mThreadId, mPageNum)
                .compose(JsonUtil.jsonSingleTransformer(AppPostsWrapper::class.java))
        val mThreadInfo = mPagerCallback?.threadInfo
        if (mThreadInfo != null) {
            postObservable = postObservable.map { it.apply { it.thread = mThreadInfo } }
        } else {
            postObservable = postObservable.observeOn(Schedulers.io())
                    .zipWith(appService.getThreadInfo(mUser.appSecureToken, mThreadId),
                            BiFunction<AppPostsWrapper, AppDataWrapper<AppThread>, AppPostsWrapper> { p0, p1 ->
                                p0.thread = p1.data
                                return@BiFunction p0
                            })
        }
        return postObservable.map {
            //二手交易区
            if (it.thread?.fid == 115) {
                val data = it.data
                //first position in first page, message is empty
                if (data != null && data.pageNo == 1) {
                    val post = data.list[0]
                    if (post.position == 1 && post.message.isNullOrBlank()) {
                        data.list[0].trade = true
                    }
                }
            }
            return@map it
        }
    }

    override fun onNext(data: AppPostsWrapper) {
        L.print(data.toString())
        mPagerCallback?.threadInfo = data.thread
        val pullUpToRefresh = isPullUpToRefresh
        var postList: List<AppPost>? = null

        val posts = data.data
        if (posts != null) {
            postList = posts.list
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
            consumeAppResult(data)
        } else {
            super.onNext(data)
            val postListInfo = data.thread
            if (postListInfo != null) {
                mRecyclerAdapter.setThreadInfo(postListInfo)
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
                val quotePostId = arguments.getString(ARG_QUOTE_POST_ID)
                if (!TextUtils.isEmpty(quotePostId)) {
                    var i = 0
                    val length = postList.size
                    while (i < length) {
                        if (Integer.parseInt(quotePostId) == postList[i].pid) {
                            // scroll to post post
                            mLayoutManager.scrollToPositionWithOffset(i, 0)
                            break
                        }
                        i++
                    }
                    // clear this argument after redirecting
                    arguments.putString(ARG_QUOTE_POST_ID, null)
                }
            }

            initQuickSidebar(mPageNum, postList.size)
        }
    }

    override fun onError(throwable: Throwable) {
        if (AppApiUtil.appLoginIfNeed(mRxBus, throwable)) {
            return
        }

        //网络请求失败下依然刷新黑名单
        if (blacklistChanged) {
            blacklistChanged = false
            RxJavaUtil.disposeIfNotNull(refreshAfterBlacklistChangeDisposable)
            val dataSet = mRecyclerAdapter.dataSet
            refreshAfterBlacklistChangeDisposable = Single.just(dataSet)
                    .map { filterPostAfterBlacklistChanged(it) }
                    .compose(RxJavaUtil.iOSingleTransformer<List<Any>>())
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

    private fun initQuickSidebar(page: Int, postSize: Int) {
        invalidateQuickSidebarVisible()
        val customLetters = ArrayList<String>()
        for (i in 0..postSize - 1) {
            //1-10, then interval 2
            if (i >= 10 && i % 2 == 0) {
                continue
            }
            val letter = (i + 1 + 30 * (page - 1)).toString()
            customLetters.add(letter)
            letters.put(letter, i)
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

        var threadInfo: AppThread?
    }

    companion object {

        private val ARG_THREAD_ID = "thread_id"
        private val ARG_PAGE_NUM = "page_num"
        private val ARG_READ_PROGRESS = "read_progress"
        private val ARG_PAGER_SCROLL_STATE = "pager_scroll_state"

        /**
         * Used for post post redirect.
         */
        private val ARG_QUOTE_POST_ID = "quote_post_id"

        fun newInstance(threadId: String, pageNum: Int): AppPostListPagerFragment {
            return newInstance(threadId, pageNum, null, null, null)
        }

        fun newInstance(threadId: String, pageNum: Int, progress: ReadProgress, scrollState: PagerScrollState): AppPostListPagerFragment {
            return newInstance(threadId, pageNum, null, progress, scrollState)
        }

        fun newInstance(threadId: String, pageNum: Int, postId: String): AppPostListPagerFragment {
            return newInstance(threadId, pageNum, postId, null, null)
        }

        private fun newInstance(threadId: String, pageNum: Int, postId: String?, progress: ReadProgress?, scrollState: PagerScrollState?): AppPostListPagerFragment {
            val fragment = AppPostListPagerFragment()
            val bundle = Bundle()
            bundle.putString(ARG_THREAD_ID, threadId)
            if (!TextUtils.isEmpty(postId)) {
                bundle.putString(ARG_QUOTE_POST_ID, postId)
            }
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
            return dataSet.filterIsInstance<AppPost>()
                    .mapNotNull { AppPostsWrapper.filterPost(it) }
        }
    }
}
