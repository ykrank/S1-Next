package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.view.*
import me.ykrank.s1next.R
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.util.ResourceUtil
import me.ykrank.s1next.util.StringUtil
import me.ykrank.s1next.view.dialog.PageJumpDialogFragment
import me.ykrank.s1next.view.internal.PagerCallback
import me.ykrank.s1next.widget.TagFragmentStatePagerAdapter

/**
 * A base Fragment wraps [ViewPager] and provides related methods.
 */
abstract class BaseViewPagerFragment : BaseFragment(), PageJumpDialogFragment.OnPageJumpedListener, PagerCallback {

    protected lateinit var mViewPager: ViewPager
    internal lateinit var mAdapter: BaseFragmentStatePagerAdapter<*>
    protected var mTotalPages: Int = 0

    private var mMenuPageJump: MenuItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_pager, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            mTotalPages = 1
        } else {
            mTotalPages = savedInstanceState.getInt(STATE_TOTAL_PAGES)
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewPager = view.findViewById(R.id.view_pager)
        mViewPager.offscreenPageLimit = 2
        loadViewPager()
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)
    }

    @CallSuper
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_view_pager, menu)

        mMenuPageJump = menu.findItem(R.id.menu_page_jump)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        preparePageJumpMenu()
    }

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_page_jump -> {
                //do not show page jump dialog if total page below 1
                if (mTotalPages <= 1) {
                    return true
                }
                PageJumpDialogFragment.newInstance(mTotalPages, currentPage).show(
                        childFragmentManager, PageJumpDialogFragment.TAG)

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    internal fun onError(throwable: Throwable) {
        showShortSnackbar(ErrorUtil.parse(context, throwable))
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(STATE_TOTAL_PAGES, mTotalPages)
    }

    internal abstract fun getPagerAdapter(fragmentManager: FragmentManager): BaseFragmentStatePagerAdapter<*>

    fun getTotalPages(): Int {
        return mTotalPages
    }

    override fun setTotalPages(totalPages: Int) {
        if (totalPages <= mTotalPages) {
            return
        }
        this.mTotalPages = totalPages
        mViewPager.adapter.notifyDataSetChanged()
        preparePageJumpMenu()
    }

    internal var currentPage: Int
        get() = mViewPager.currentItem
        set(currentPage) {
            mViewPager.currentItem = currentPage
        }

    internal fun loadViewPager() {
        // don't use getChildFragmentManager()
        // because we can't retain Fragments (DataRetainedFragment)
        // that are nested in other fragments
        mAdapter = getPagerAdapter(fragmentManager)
        mViewPager.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

    @CallSuper
    override fun onPageJumped(position: Int) {
        mViewPager.currentItem = position
    }

    /**
     * Disables the page jump menu if only has one page.
     */
    private fun preparePageJumpMenu() {
        mMenuPageJump?.isEnabled = mTotalPages != 1
    }

    internal fun setTitleWithPosition(position: Int) {
        val titleWithoutPosition = getTitleWithoutPosition()
        if (titleWithoutPosition == null) {
            activity.title = null
            return
        }

        val titleWithPosition: String
        if (ResourceUtil.isRTL(resources)) {
            titleWithPosition = StringUtil.concatWithTwoSpaces(position + 1, titleWithoutPosition)
        } else {
            titleWithPosition = StringUtil.concatWithTwoSpaces(titleWithoutPosition, position + 1)
        }
        activity.title = titleWithPosition
    }

    internal abstract fun getTitleWithoutPosition(): CharSequence?

    /**
     * A base [TagFragmentStatePagerAdapter] wraps some implement.
     */
    internal abstract inner class BaseFragmentStatePagerAdapter<T : BaseRecyclerViewFragment<*>>(fm: FragmentManager) : TagFragmentStatePagerAdapter<T>(fm) {

        var currentFragment: T? = null
            private set

        override fun getCount(): Int {
            return mTotalPages
        }

        @CallSuper
        override fun setPrimaryItem(container: ViewGroup, position: Int, fragment: T) {
            setTitleWithPosition(position)
            if (currentFragment !== fragment) {
                currentFragment = fragment
            }

            super.setPrimaryItem(container, position, fragment)
        }

        @CallSuper
        override fun destroyItem(container: ViewGroup, position: Int, fragment: T?) {
            fragment?.destroyRetainedFragment()

            super.destroyItem(container, position, fragment)
        }
    }

    companion object {

        /**
         * The serialization (saved instance state) Bundle key representing
         * the total pages.
         */
        private val STATE_TOTAL_PAGES = "total_pages"
    }
}
