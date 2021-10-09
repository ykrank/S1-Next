package com.github.ykrank.androidtools.ui

import android.os.Bundle
import android.view.*
import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.github.ykrank.androidtools.GlobalData
import com.github.ykrank.androidtools.R
import com.github.ykrank.androidtools.ui.dialog.PageJumpDialogFragment
import com.github.ykrank.androidtools.ui.internal.PagerCallback
import com.github.ykrank.androidtools.widget.TagFragmentStatePagerAdapter

/**
 * A base Fragment wraps [ViewPager] and provides related methods.
 */
abstract class LibBaseViewPagerFragment : LibBaseFragment(), PagerCallback,
    PageJumpDialogFragment.OnPageJumpedListener {

    private lateinit var mViewPager: ViewPager
    protected lateinit var mAdapter: LibBaseFragmentStatePagerAdapter<*>
    protected var mTotalPages: Int = 0

    private var mMenuPageJump: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
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

        mViewPager = findViewPager(view)
        mViewPager.offscreenPageLimit = 2
        loadViewPager()
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_jump_page, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        mMenuPageJump = findMenuPageJump(menu)
        preparePageJumpMenu()
    }

    open fun findMenuPageJump(menu: Menu): MenuItem? {
        return menu.findItem(R.id.menu_page_jump)
    }

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            mMenuPageJump?.itemId -> {
                //do not show page jump dialog if total page below 1
                if (mTotalPages <= 1) {
                    return true
                }
                PageJumpDialogFragment.newInstance(mTotalPages, currentPage).show(
                    childFragmentManager, PageJumpDialogFragment.TAG
                )

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    protected open fun onError(throwable: Throwable) {
        GlobalData.provider.errorParser?.let {
            val context = context
            if (context != null)
                showShortSnackbar(it.parse(context, throwable))
        }
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(STATE_TOTAL_PAGES, mTotalPages)
    }

    abstract fun getPagerAdapter(fragmentManager: FragmentManager): LibBaseFragmentStatePagerAdapter<*>

    open fun findViewPager(rootView: View): ViewPager {
        return rootView.findViewById(R.id.view_pager)
    }

    open fun getTotalPages(): Int {
        return mTotalPages
    }

    override fun setTotalPages(totalPages: Int) {
        if (totalPages <= mTotalPages) {
            return
        }
        this.mTotalPages = totalPages
        mViewPager.adapter?.notifyDataSetChanged()
        preparePageJumpMenu()
    }

    open var currentPage: Int
        get() = mViewPager.currentItem
        set(currentPage) {
            mViewPager.currentItem = currentPage
        }

    open fun loadViewPager() {
        // don't use getChildFragmentManager()
        // because we can't retain Fragments (DataRetainedFragment)
        // that are nested in other fragments
        mAdapter = getPagerAdapter(fragmentManager!!)
        mViewPager.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

    @CallSuper
    override fun onPageJumped(position: Int) {
        mViewPager.currentItem = position
    }

    fun moveToNext(moved: Int = 1) {
        mViewPager.currentItem = mViewPager.currentItem + moved
    }

    /**
     * Disables the page jump menu if only has one page.
     */
    private fun preparePageJumpMenu() {
        mMenuPageJump?.isEnabled = mTotalPages != 1
    }

    protected open fun setTitleWithPosition(position: Int) {

    }

    protected open fun getTitleWithoutPosition(): CharSequence? {
        return null
    }

    /**
     * A base [TagFragmentStatePagerAdapter] wraps some implement.
     */
    abstract inner class FragmentStatePagerAdapter<T : LibBaseFragment>(fm: FragmentManager) :
        LibBaseFragmentStatePagerAdapter<T>(fm) {

        override fun getCount(): Int {
            return mTotalPages
        }

        @CallSuper
        override fun setPrimaryItem(container: ViewGroup, position: Int, fragment: T) {
            setTitleWithPosition(position)
            super.setPrimaryItem(container, position, fragment)
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
