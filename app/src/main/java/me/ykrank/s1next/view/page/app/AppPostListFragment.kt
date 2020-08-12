package me.ykrank.s1next.view.page.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentManager
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.ui.internal.CoordinatorLayoutAnchorDelegate
import com.github.ykrank.androidtools.util.ClipboardUtil
import com.github.ykrank.androidtools.util.StringUtil
import com.github.ykrank.androidtools.widget.RxBus
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.app.model.AppThread
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.util.IntentUtil
import me.ykrank.s1next.view.activity.BaseActivity
import me.ykrank.s1next.view.activity.NewRateActivity
import me.ykrank.s1next.view.activity.NewReportActivity
import me.ykrank.s1next.view.activity.ReplyActivity
import me.ykrank.s1next.view.dialog.LoginPromptDialogFragment
import me.ykrank.s1next.view.dialog.PostSelectableChangeDialogFragment
import me.ykrank.s1next.view.dialog.ThreadFavouritesAddDialogFragment
import me.ykrank.s1next.view.dialog.VoteDialogFragment
import me.ykrank.s1next.view.event.*
import me.ykrank.s1next.view.fragment.BaseViewPagerFragment
import me.ykrank.s1next.view.internal.PagerScrollState
import me.ykrank.s1next.view.internal.RequestCode
import me.ykrank.s1next.view.page.edit.EditPostActivity
import javax.inject.Inject


/**
 * A Fragment includes [android.support.v4.view.ViewPager]
 * to represent each page of post lists.
 */
class AppPostListFragment : BaseViewPagerFragment(), AppPostListPagerFragment.PagerCallback, View.OnClickListener {

    @Inject
    internal lateinit var mRxBus: RxBus

    @Inject
    internal lateinit var mGeneralPreferencesManager: GeneralPreferencesManager

    @Inject
    internal lateinit var mDownloadPrefManager: DownloadPreferencesManager

    private lateinit var mThreadId: String
    private var mThreadTitle: String? = null

    private val scrollState = PagerScrollState()

    private val mPostListPagerAdapter: PostListPagerAdapter by lazy { PostListPagerAdapter(childFragmentManager) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.appComponent.inject(this)

        val bundle = arguments!!
        val type = bundle.getInt(ARG_TYPE)
        mThreadId = bundle.getString(ARG_THREAD_ID) as String

        leavePageMsg("AppPostListFragment##ThreadTitle:$mThreadTitle,ThreadId:$mThreadId,Type:$type")

        if (savedInstanceState == null) {
            val jumpPage = bundle.getInt(ARG_JUMP_PAGE, 0)

            if (jumpPage != 0) {
                currentPage = jumpPage - 1
            }
        }

        (activity as CoordinatorLayoutAnchorDelegate).setupFloatingActionButton(
                R.drawable.ic_insert_comment_black_24dp, this)
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
                .ofType(ReportEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.PAUSE))
                .subscribe { event -> startReportActivity(event.threadId, event.postId, event.pageNum) }

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
                    if (!LoginPromptDialogFragment.showAppLoginPromptDialogIfNeeded(fragmentManager!!, mUser)) {
                        VoteDialogFragment.newInstance(it.threadId, it.vote).show(fragmentManager!!, VoteDialogFragment.TAG)
                    }
                }
        mRxBus.get()
                .ofType(BlackListChangeEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.PAUSE))
                .subscribe { activity?.setResult(Activity.RESULT_OK) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_post, menu)

        menu.findItem(R.id.menu_thread_attachment).isVisible = false
        menu.findItem(R.id.menu_save_progress).isVisible = false
        menu.findItem(R.id.menu_load_progress).isVisible = false
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val mMenuPostSelectable = menu.findItem(R.id.menu_post_selectable)
        mMenuPostSelectable?.isChecked = mGeneralPreferencesManager.isPostSelectable
        val mMenuQuickSideBarEnable = menu.findItem(R.id.menu_quick_side_bar_enable)
        mMenuQuickSideBarEnable?.isChecked = mGeneralPreferencesManager.isQuickSideBarEnable
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favourites_add -> {
                if (!LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(fragmentManager!!, mUser)) {
                    ThreadFavouritesAddDialogFragment.newInstance(mThreadId, mThreadTitle).show(
                            activity!!.supportFragmentManager,
                            ThreadFavouritesAddDialogFragment.TAG)
                }

                return true
            }
            R.id.menu_link -> {
                ClipboardUtil.copyText(context, "Url of $mThreadTitle", Api.getPostListUrlForBrowser(mThreadId,
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
            R.id.menu_post_selectable -> {
                //Switch text selectable
                PostSelectableChangeDialogFragment.newInstance(!item.isChecked)
                        .setPositiveListener { dialog, which ->
                            //reload all data
                            item.isChecked = !item.isChecked
                            mGeneralPreferencesManager.isPostSelectable = item.isChecked
                            mRxBus.post(PostSelectableChangeEvent())
                        }
                        .show(fragmentManager!!, null)
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

    override fun getPagerAdapter(fragmentManager: FragmentManager): FragmentStatePagerAdapter<*> {
        return mPostListPagerAdapter
    }

    override fun getTitleWithoutPosition(): CharSequence? {
        return mThreadTitle
    }

    override var threadInfo: AppThread? = null
        set(value) {
            if (value != null && field != value) {
                field = value
                setThreadTitle(value.subject)
            }
        }

    override fun setTotalPages(page: Int?) {
        if (page != null) {
            super.setTotalPages(page)
        }
    }

    private fun setThreadTitle(title: CharSequence?) {
        if (!title.isNullOrEmpty() && mThreadTitle != title.toString()) {
            mThreadTitle = title.toString()
            setTitleWithPosition(currentPage)
        }
    }

    override fun onClick(v: View) {
        startReplyActivity(null, null)
    }

    /**
     * 获取当前的具体帖子fragment
     */
    internal val curPostPageFragment: AppPostListPagerFragment?
        get() = mPostListPagerAdapter.currentFragment


    private fun startReplyActivity(quotePostId: String?, quotePostCount: String?) {
        val fm = fragmentManager ?: return
        val activity = activity ?: return
        if (LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(fm, mUser)) {
            return
        }

        ReplyActivity.startReplyActivityForResultMessage(activity, mThreadId, mThreadTitle,
                quotePostId, quotePostCount)
    }

    private fun startRateActivity(threadId: String, postId: String) {
        val fm = fragmentManager ?: return
        val activity = activity ?: return
        if (LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(fm, mUser)) {
            return
        }

        NewRateActivity.start(activity, threadId, postId)
    }

    private fun startReportActivity(threadId: String, postId: String, pageNum: Int) {
        val fm = fragmentManager ?: return
        val activity = activity ?: return
        if (LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(fm, mUser)) {
            return
        }

        NewReportActivity.start(activity, threadId, postId, pageNum)
    }

    /**
     * Returns a Fragment corresponding to one of the pages of posts.
     */
    private inner class PostListPagerAdapter constructor(fm: FragmentManager) : FragmentStatePagerAdapter<AppPostListPagerFragment>(fm) {

        override fun getItem(i: Int): AppPostListPagerFragment {
            val bundle = arguments!!
            val jumpPage = bundle.getInt(ARG_JUMP_PAGE, -1)
            val quotePostId = bundle.getString(ARG_QUOTE_POST_ID)
            if (jumpPage == i + 1 && !TextUtils.isEmpty(quotePostId)) {
                // clear this arg string because we only need to tell AppPostListPagerFragment once
                arguments?.putString(ARG_QUOTE_POST_ID, null)
                return AppPostListPagerFragment.newInstance(mThreadId, jumpPage, quotePostId)
            } else {
                return AppPostListPagerFragment.newInstance(mThreadId, i + 1, quotePostId)
            }
        }
    }

    companion object {
        val TAG: String = AppPostListFragment::class.java.name

        const val Type_Thread = 0
        const val Type_Thread_One_Author = 3

        private const val ARG_TYPE = "type"
        private const val ARG_THREAD_ID = "thread_id"

        /**
         * ARG_JUMP_PAGE takes precedence over [.ARG_SHOULD_GO_TO_LAST_PAGE].
         */
        private const val ARG_JUMP_PAGE = "jump_page"
        private const val ARG_QUOTE_POST_ID = "quote_post_id"

        fun newInstance(threadId: String, jumpPage: Int?, quotePostId: String?): AppPostListFragment {
            val fragment = AppPostListFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_TYPE, Type_Thread_One_Author)
            bundle.putString(ARG_THREAD_ID, threadId)
            if (jumpPage != null) {
                bundle.putInt(ARG_JUMP_PAGE, jumpPage)
            }
            bundle.putString(ARG_QUOTE_POST_ID, quotePostId)
            fragment.arguments = bundle

            return fragment
        }
    }
}
