package me.ykrank.s1next.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.google.common.base.Preconditions
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.api.model.ThreadLink
import me.ykrank.s1next.data.api.model.collection.Posts
import me.ykrank.s1next.data.db.HistoryDbWrapper
import me.ykrank.s1next.data.db.ReadProgressDbWrapper
import me.ykrank.s1next.data.db.ThreadDbWrapper
import me.ykrank.s1next.data.db.dbmodel.DbThread
import me.ykrank.s1next.data.db.dbmodel.History
import me.ykrank.s1next.data.db.dbmodel.ReadProgress
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.data.pref.ReadProgressPreferencesManager
import me.ykrank.s1next.util.*
import me.ykrank.s1next.view.activity.BaseActivity
import me.ykrank.s1next.view.activity.EditPostActivity
import me.ykrank.s1next.view.activity.NewRateActivity
import me.ykrank.s1next.view.activity.ReplyActivity
import me.ykrank.s1next.view.dialog.*
import me.ykrank.s1next.view.event.*
import me.ykrank.s1next.view.internal.CoordinatorLayoutAnchorDelegate
import me.ykrank.s1next.view.internal.PagerScrollState
import me.ykrank.s1next.view.internal.RequestCode
import me.ykrank.s1next.widget.RxBus
import me.ykrank.s1next.widget.track.event.ViewThreadTrackEvent
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * A Fragment includes [android.support.v4.view.ViewPager]
 * to represent each page of post lists.
 */
class PostListFragment : BaseViewPagerFragment(), PostListPagerFragment.PagerCallback, View.OnClickListener {

    @Inject
    internal lateinit var mRxBus: RxBus

    @Inject
    internal lateinit var mGeneralPreferencesManager: GeneralPreferencesManager

    @Inject
    internal lateinit var mReadProgressPrefManager: ReadProgressPreferencesManager

    @Inject
    internal lateinit var historyDbWrapper: HistoryDbWrapper

    private lateinit var mThreadId: String
    private var mThreadTitle: String? = null

    private var mThreadAttachment: Posts.ThreadAttachment? = null
    private var mMenuThreadAttachment: MenuItem? = null

    private var readProgress: ReadProgress? = null
    private var tempReadProgress: ReadProgress? = null
    private val scrollState = PagerScrollState()

    private lateinit var mLastThreadInfoSubject: PublishSubject<Int>

    private val mPostListPagerAdapter: PostListPagerAdapter by lazy { PostListPagerAdapter(fragmentManager) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.getAppComponent().inject(this)

        val bundle = arguments
        val thread = Preconditions.checkNotNull(bundle.getParcelable<Thread>(ARG_THREAD))
        // thread title is null if this thread comes from ThreadLink
        mThreadTitle = thread.title
        mThreadId = thread.id
        trackAgent.post(ViewThreadTrackEvent(mThreadTitle, mThreadId))
        L.leaveMsg("PostListFragment##ThreadTitle:$mThreadTitle,ThreadId:$mThreadId")

        if (savedInstanceState == null) {
            val jumpPage: Int
            //读取进度
            readProgress = bundle.getParcelable<ReadProgress>(ARG_READ_PROGRESS)
            if (readProgress != null) {
                scrollState.state = PagerScrollState.BEFORE_SCROLL_POSITION
                jumpPage = readProgress?.page ?: 0
            } else {
                jumpPage = bundle.getInt(ARG_JUMP_PAGE, 0)
            }

            if (jumpPage != 0) {
                // we do not know the total page if we open this thread by URL
                // so we set the jump page to total page
                setTotalPages(jumpPage)
                currentPage = jumpPage - 1
            } else {
                // +1 for original post
                setTotalPageByPosts(thread.reliesCount + 1)
                if (bundle.getBoolean(ARG_SHOULD_GO_TO_LAST_PAGE, false)) {
                    currentPage = getTotalPages() - 1
                }
            }
            saveHistory()
        }

        (activity as CoordinatorLayoutAnchorDelegate).setupFloatingActionButton(
                R.drawable.ic_insert_comment_black_24dp, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLastThreadInfoSubject = PublishSubject.create<Int>()
        mLastThreadInfoSubject.throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.DESTROY))
                .subscribe({
                    LooperUtil.enforceOnWorkThread()
                    val dbThread = DbThread(Integer.valueOf(mThreadId), it)
                    ThreadDbWrapper.getInstance().saveThread(dbThread)
                }, { L.report(it) })
    }

    override fun onResume() {
        super.onResume()

        mRxBus.get()
                .ofType(QuoteEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.PAUSE))
                .subscribe { quoteEvent -> startReplyActivity(quoteEvent.quotePostId, quoteEvent.quotePostCount) }
        mRxBus.get()
                .ofType(RateEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.PAUSE))
                .subscribe { event -> startRateActivity(event.threadId, event.postId) }
        mRxBus.get()
                .ofType(EditPostEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.PAUSE))
                .subscribe {
                    val thread = it.thread
                    val post = it.post
                    EditPostActivity.startActivityForResultMessage(this, RequestCode.REQUEST_CODE_EDIT_POST, thread, post)
                }
        mRxBus.get()
                .ofType(VotePostEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.PAUSE))
                .subscribe {
                    if (!LoginPromptDialogFragment.showAppLoginPromptDialogIfNeeded(fragmentManager, mUser)) {
                        VoteDialogFragment.newInstance(it.threadId, it.vote).show(fragmentManager, VoteDialogFragment.TAG)
                    }
                }
        mRxBus.get()
                .ofType(BlackListChangeEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.PAUSE))
                .subscribe { activity.setResult(Activity.RESULT_OK) }
    }

    override fun onPause() {
        //save last read progress
        val fragment = curPostPageFragment
        if (fragment != null) {
            tempReadProgress = fragment.curReadProgress
            if (tempReadProgress != null) {
                Single.just(tempReadProgress)
                        .delay(5, TimeUnit.SECONDS)
                        .doOnError(L::report)
                        .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                        .subscribe { b ->
                            mReadProgressPrefManager.saveLastReadProgress(b)
                            L.i("Save last read progress:" + b)
                        }
            }
        } else {
            tempReadProgress = null
        }
        super.onPause()
    }

    override fun onDestroy() {
        mReadProgressPrefManager.saveLastReadProgress(null)

        //Auto save read progress
        if (mReadProgressPrefManager.isSaveAuto) {
            tempReadProgress?.let { PostListPagerFragment.saveReadProgressBack(it) }
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_post, menu)

        mMenuThreadAttachment = menu.findItem(R.id.menu_thread_attachment)
        if (mThreadAttachment == null) {
            mMenuThreadAttachment?.isVisible = false
        }

        if (mReadProgressPrefManager.isSaveAuto) {
            val saveMenu = menu.findItem(R.id.menu_save_progress)
            saveMenu?.isVisible = false
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val mMenuPostSelectable = menu?.findItem(R.id.menu_post_selectable)
        mMenuPostSelectable?.isChecked = mGeneralPreferencesManager.isPostSelectable
        val mMenuQuickSideBarEnable = menu?.findItem(R.id.menu_quick_side_bar_enable)
        mMenuQuickSideBarEnable?.isChecked = mGeneralPreferencesManager.isQuickSideBarEnable
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_thread_attachment -> {
                ThreadAttachmentDialogFragment.newInstance(mThreadAttachment).show(
                        activity.supportFragmentManager,
                        ThreadAttachmentDialogFragment.TAG)

                return true
            }
            R.id.menu_favourites_add -> {
                if (!LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(fragmentManager, mUser)) {
                    ThreadFavouritesAddDialogFragment.newInstance(mThreadId).show(
                            activity.supportFragmentManager,
                            ThreadFavouritesAddDialogFragment.TAG)
                }

                return true
            }
            R.id.menu_link -> {
                ClipboardUtil.copyText(context, Api.getPostListUrlForBrowser(mThreadId,
                        currentPage))
                (activity as CoordinatorLayoutAnchorDelegate).showShortSnackbar(
                        R.string.message_thread_link_copy)

                return true
            }
            R.id.menu_share -> {
                val value: String
                val url = Api.getPostListUrlForBrowser(mThreadId, currentPage)
                if (TextUtils.isEmpty(mThreadTitle)) {
                    value = url
                } else {
                    value = StringUtil.concatWithTwoSpaces(mThreadTitle, url)
                }

                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, value)
                intent.type = "text/plain"

                startActivity(Intent.createChooser(intent, getString(R.string.menu_title_share)))

                return true
            }
            R.id.menu_browser -> {
                IntentUtil.startViewIntentExcludeOurApp(context, Uri.parse(
                        Api.getPostListUrlForBrowser(mThreadId, currentPage + 1)))

                return true
            }
            R.id.menu_save_progress -> {
                if (curPostPageFragment != null) {
                    curPostPageFragment?.saveReadProgress()
                }
                return true
            }
            R.id.menu_load_progress -> {
                loadReadProgress()
                return true
            }
            R.id.menu_post_selectable -> {
                //Switch text selectable
                PostSelectableChangeDialogFragment.newInstance(!item.isChecked)
                        .setPositiveListener { dialog, which ->
                            //reload all data
                            item.isChecked = !item.isChecked
                            mGeneralPreferencesManager.isPostSelectable = item.isChecked
                            mRxBus.post(PostSelectableChangeEvent())
                        }
                        .show(fragmentManager, null)
                return true
            }
            R.id.menu_quick_side_bar_enable -> {
                item.isChecked = !item.isChecked
                mGeneralPreferencesManager.isQuickSideBarEnable = item.isChecked
                mRxBus.post(QuickSidebarEnableChangeEvent())
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.REQUEST_CODE_EDIT_POST) {
            if (resultCode == Activity.RESULT_OK) {
                val msg = data?.getStringExtra(BaseActivity.EXTRA_MESSAGE)
                showShortSnackbar(msg)
                val fragment = curPostPageFragment
                fragment?.startSwipeRefresh()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun getPagerAdapter(fragmentManager: FragmentManager): BaseViewPagerFragment.BaseFragmentStatePagerAdapter<*> {
        return mPostListPagerAdapter
    }

    override fun getTitleWithoutPosition(): CharSequence? {
        return mThreadTitle
    }

    override var threadInfo: Thread? = null
        get() = field
        set(value) {
            if (value != null && field != value) {
                field = value
                setThreadTitle(value.title)
                setTotalPageByPosts(value.reliesCount + 1)
            }
        }

    private fun setTotalPageByPosts(threads: Int) {
        setTotalPages(MathUtil.divide(threads, Api.POSTS_PER_PAGE))
        //save reply count in database
        try {
            mLastThreadInfoSubject.onNext(threads - 1)
        } catch (e: Exception) {
            mLastThreadInfoSubject.onError(e)
        }

    }

    private fun setThreadTitle(title: CharSequence?) {
        if (!title.isNullOrEmpty() && mThreadTitle != title.toString()) {
            mThreadTitle = title.toString()
            setTitleWithPosition(currentPage)
            saveHistory()
        }
    }

    override fun setupThreadAttachment(threadAttachment: Posts.ThreadAttachment) {
        this.mThreadAttachment = threadAttachment

        // mMenuThreadAttachment = null when configuration changes (like orientation changes)
        // but we don't need to care about the visibility of mMenuThreadAttachment
        // because mThreadAttachment != null and we won't invoke
        // mMenuThreadAttachment.setVisible(false) during onCreateOptionsMenu(Menu)
        mMenuThreadAttachment?.isVisible = true
    }

    override fun onClick(v: View) {
        startReplyActivity(null, null)
    }

    /**
     * 获取当前的具体帖子fragment
     */
    internal val curPostPageFragment: PostListPagerFragment?
        get() = mPostListPagerAdapter.currentFragment

    /**
     * 读取阅读进度
     */
    internal fun loadReadProgress() {
        Single.just(mThreadId)
                .flatMap {
                    val dbWrapper = ReadProgressDbWrapper.getInstance()
                    val progress: ReadProgress? = dbWrapper.getWithThreadId(Integer.valueOf(it))
                    if (progress == null) Single.never<ReadProgress>() else Single.just(progress)
                }
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.PAUSE))
                .subscribe({
                    scrollState.state = PagerScrollState.BEFORE_SCROLL_PAGE
                    this.afterLoadReadProgress(it)
                }, L::report)
    }

    /**
     * 读取阅读进度后的操作，主线程
     */
    @MainThread
    private fun afterLoadReadProgress(progress: ReadProgress?) {
        if (progress != null && scrollState.state == PagerScrollState.BEFORE_SCROLL_PAGE) {
            val targetPosition = progress.page - 1
            if (targetPosition < 0) {
                //readProgress page error
                return
            }
            val fragment = mPostListPagerAdapter.getCachedFragment(targetPosition)
            if (fragment != null) {
                if (currentPage != targetPosition) {
                    fragment.loadReadProgressInRecycleView(progress, false)
                    currentPage = progress.page - 1
                } else {
                    fragment.loadReadProgressInRecycleView(progress, true)
                }
                scrollState.state = PagerScrollState.FREE
            } else {
                scrollState.state = PagerScrollState.BEFORE_SCROLL_POSITION
                currentPage = progress.page - 1
            }
        }
    }

    private fun startReplyActivity(quotePostId: String?, quotePostCount: String?) {
        if (LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(fragmentManager, mUser)) {
            return
        }

        ReplyActivity.startReplyActivityForResultMessage(activity, mThreadId, mThreadTitle,
                quotePostId, quotePostCount)
    }

    private fun startRateActivity(threadId: String?, postId: String?) {
        if (LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(fragmentManager, mUser)) {
            return
        }

        NewRateActivity.start(activity, threadId, postId)
    }

    private fun saveHistory() {
        val threadId = Integer.valueOf(mThreadId)
        if (threadId > 0 && !TextUtils.isEmpty(mThreadTitle)) {
            historyDbWrapper.addNewHistory(History(threadId, mThreadTitle))
        }
    }

    /**
     * Returns a Fragment corresponding to one of the pages of posts.
     */
    private inner class PostListPagerAdapter constructor(fm: FragmentManager) : BaseViewPagerFragment.BaseFragmentStatePagerAdapter<PostListPagerFragment>(fm) {

        override fun getItem(i: Int): PostListPagerFragment {
            val progress = readProgress
            val bundle = arguments
            val jumpPage = bundle.getInt(ARG_JUMP_PAGE, -1)
            val quotePostId = bundle.getString(ARG_QUOTE_POST_ID)
            if (jumpPage == i + 1 && !TextUtils.isEmpty(quotePostId)) {
                // clear this arg string because we only need to tell PostListPagerFragment once
                bundle.putString(ARG_QUOTE_POST_ID, null)
                return PostListPagerFragment.newInstance(mThreadId, jumpPage, quotePostId)
            } else if (progress != null && progress.page == i + 1
                    && scrollState.state == PagerScrollState.BEFORE_SCROLL_POSITION) {
                return PostListPagerFragment.newInstance(mThreadId, i + 1, progress, scrollState)
            } else {
                return PostListPagerFragment.newInstance(mThreadId, i + 1)
            }
        }
    }

    companion object {

        val TAG = PostListFragment::class.java.name

        private val ARG_THREAD = "thread"
        private val ARG_SHOULD_GO_TO_LAST_PAGE = "should_go_to_last_page"

        /**
         * ARG_JUMP_PAGE takes precedence over [.ARG_SHOULD_GO_TO_LAST_PAGE].
         */
        private val ARG_JUMP_PAGE = "jump_page"
        private val ARG_QUOTE_POST_ID = "quote_post_id"

        private val ARG_READ_PROGRESS = "read_progress"

        fun newInstance(thread: Thread, shouldGoToLastPage: Boolean): PostListFragment {
            val fragment = PostListFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_THREAD, thread)
            bundle.putBoolean(ARG_SHOULD_GO_TO_LAST_PAGE, shouldGoToLastPage)
            fragment.arguments = bundle

            return fragment
        }

        fun newInstance(threadLink: ThreadLink): PostListFragment {
            val thread = Thread()
            thread.id = threadLink.threadId

            val fragment = PostListFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_THREAD, thread)
            bundle.putInt(ARG_JUMP_PAGE, threadLink.jumpPage)
            val quotePostId = threadLink.quotePostId
            if (quotePostId.isPresent) {
                bundle.putString(ARG_QUOTE_POST_ID, quotePostId.get())
            }
            fragment.arguments = bundle

            return fragment
        }

        fun newInstance(thread: Thread, progress: ReadProgress): PostListFragment {
            val fragment = PostListFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_THREAD, thread)
            bundle.putParcelable(ARG_READ_PROGRESS, progress)
            fragment.arguments = bundle

            return fragment
        }
    }
}
