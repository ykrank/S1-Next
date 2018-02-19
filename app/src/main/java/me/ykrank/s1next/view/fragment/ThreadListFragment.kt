package me.ykrank.s1next.view.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.github.ykrank.androidtools.ui.LibBaseViewPagerFragment
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.MathUtil
import com.google.common.base.Preconditions
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.model.Forum
import me.ykrank.s1next.util.IntentUtil

/**
 * A Fragment includes [android.support.v4.view.ViewPager]
 * to represent each page of thread lists.
 */
class ThreadListFragment : BaseViewPagerFragment(), ThreadListPagerFragment.PagerCallback {

    private var mForumName: String? = null
    private lateinit var mForumId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val forum = Preconditions.checkNotNull(arguments!!.getParcelable<Forum>(ARG_FORUM))
        mForumName = forum.name
        mForumId = forum.id
        L.leaveMsg("ThreadListFragment##ForumName:$mForumName,ForumId:$mForumId")

        if (savedInstanceState == null) {
            setTotalPageByThreads(forum.threads)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_thread, menu)

        menu?.findItem(R.id.menu_page_jump)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_browser -> {
                IntentUtil.startViewIntentExcludeOurApp(context, Uri.parse(
                        Api.getThreadListUrlForBrowser(mForumId, currentPage + 1)))

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun getPagerAdapter(fragmentManager: FragmentManager)
            : LibBaseViewPagerFragment.BaseFragmentStatePagerAdapter<*> {
        return ThreadListPagerAdapter(fragmentManager)
    }

    override fun getTitleWithoutPosition(): CharSequence? {
        return mForumName
    }

    override fun setTotalPageByThreads(threads: Int) {
        setTotalPages(MathUtil.divide(threads, Api.THREADS_PER_PAGE))
    }

    /**
     * Returns a Fragment corresponding to one of the pages of threads.
     */
    private inner class ThreadListPagerAdapter(fm: FragmentManager)
        : LibBaseViewPagerFragment.BaseFragmentStatePagerAdapter<ThreadListPagerFragment>(fm) {

        override fun getItem(i: Int): ThreadListPagerFragment {
            return ThreadListPagerFragment.newInstance(mForumId, i + 1)
        }
    }

    companion object {

        val TAG = ThreadListFragment::class.java.name

        private val ARG_FORUM = "forum"

        fun newInstance(forum: Forum): ThreadListFragment {
            val fragment = ThreadListFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_FORUM, forum)
            fragment.arguments = bundle

            return fragment
        }
    }
}
