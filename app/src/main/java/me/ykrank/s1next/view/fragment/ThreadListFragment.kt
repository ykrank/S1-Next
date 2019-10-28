package me.ykrank.s1next.view.fragment

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.github.ykrank.androidtools.ui.LibBaseViewPagerFragment
import com.github.ykrank.androidtools.util.MathUtil
import com.github.ykrank.androidtools.widget.RxBus
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.model.Forum
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.util.IntentUtil
import me.ykrank.s1next.view.event.PostDisableStickyChangeEvent
import me.ykrank.s1next.view.event.QuickSidebarEnableChangeEvent
import javax.inject.Inject

/**
 * A Fragment includes [android.support.v4.view.ViewPager]
 * to represent each page of thread lists.
 */
class ThreadListFragment : BaseViewPagerFragment(), ThreadListPagerFragment.PagerCallback {

    private var mForumName: String? = null
    private var mTypeId: String = "0"
    private lateinit var mForumId: String

    @Inject
    internal lateinit var mRxBus: RxBus
    @Inject
    internal lateinit var mGeneralPreferencesManager: GeneralPreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.appComponent.inject(this)

        val forum = arguments!!.getParcelable(ARG_FORUM) as Forum
        mForumName = forum.name
        mForumId = forum.id!!
        leavePageMsg("ThreadListFragment##ForumName:$mForumName,ForumId:$mForumId")

        if (savedInstanceState == null) {
            setTotalPageByThreads(forum.threads)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_thread, menu)

        menu.findItem(R.id.menu_page_jump)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        val mMenuPostDisableSticky = menu.findItem(R.id.menu_post_disable_sticky)
        mMenuPostDisableSticky?.isChecked = mGeneralPreferencesManager.isPostDisableSticky
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_browser -> {
                IntentUtil.startViewIntentExcludeOurApp(context, Uri.parse(
                        Api.getThreadListUrlForBrowser(mForumId, currentPage + 1)))

                return true
            }
            R.id.menu_post_disable_sticky -> {
                item.isChecked = !item.isChecked
                mGeneralPreferencesManager.isPostDisableSticky = item.isChecked
                mRxBus.post(PostDisableStickyChangeEvent())
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun getPagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager)
            : FragmentStatePagerAdapter<*> {
        return ThreadListPagerAdapter(childFragmentManager)
    }

    override fun getTitleWithoutPosition(): CharSequence? {
        return mForumName
    }

    override fun setTotalPageByThreads(threads: Int) {
        setTotalPages(MathUtil.divide(threads, Api.THREADS_PER_PAGE))
    }

    fun changeTypeId(typeId: String?) {
        mTypeId = typeId ?: "0"
    }

    /**
     * Returns a Fragment corresponding to one of the pages of threads.
     */
    private inner class ThreadListPagerAdapter(fm: androidx.fragment.app.FragmentManager)
        : FragmentStatePagerAdapter<ThreadListPagerFragment>(fm) {

        override fun getItem(i: Int): ThreadListPagerFragment {
            return ThreadListPagerFragment.newInstance(mForumId, mTypeId, i + 1)
        }
    }

    companion object {

        val TAG = ThreadListFragment::class.java.name

        private const val ARG_FORUM = "forum"

        fun newInstance(forum: Forum): ThreadListFragment {
            val fragment = ThreadListFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_FORUM, forum)
            fragment.arguments = bundle

            return fragment
        }
    }
}
