package me.ykrank.s1next.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.ListPopupWindow
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.ActivityEvent
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.github.ykrank.androidtools.widget.net.WifiBroadcastReceiver
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.Forum
import me.ykrank.s1next.data.api.model.ThreadType
import me.ykrank.s1next.view.adapter.SubForumArrayAdapter
import me.ykrank.s1next.view.event.ThreadTypeChangeEvent
import me.ykrank.s1next.view.fragment.ThreadListFragment
import me.ykrank.s1next.view.fragment.ThreadListPagerFragment
import me.ykrank.s1next.widget.track.event.RandomImageTrackEvent
import me.ykrank.s1next.widget.track.event.ViewForumTrackEvent
import javax.inject.Inject

/**
 * An Activity shows the thread lists.
 */
class ThreadListActivity : BaseActivity(), ThreadListPagerFragment.SubForumsCallback, WifiBroadcastReceiver.NeedMonitorWifi {

    @Inject
    internal lateinit var mS1Service: S1Service

    private var mListPopupWindow: ListPopupWindow? = null
    private var mSubForumArrayAdapter: SubForumArrayAdapter? = null

    private lateinit var forum: Forum
    private lateinit var tabLayout: TabLayout
    private var refreshBlackList = false

    private var threadTypes: ArrayList<ThreadType>? = null

    private var fragment: ThreadListFragment? = null

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        App.appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thead_list)

        tabLayout = findViewById(R.id.tab)
        initTabLayout()
        disableDrawerIndicator()

        val forum = intent.getParcelableExtra<Forum?>(ARG_FORUM)
        if (forum == null) {
            L.report(IllegalStateException("ThreadListActivity intent forum is null"))
            finish()
            return
        }
        this.forum = forum
        trackAgent.post(ViewForumTrackEvent(forum.id, forum.name))
        L.leaveMsg("ThreadListActivity##forum:$forum")

        if (savedInstanceState == null) {
            fragment = ThreadListFragment.newInstance(forum)
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, fragment!!,
                    ThreadListFragment.TAG).commit()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(ThreadListFragment.TAG) as ThreadListFragment?
        }

        init()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_thread, menu)

        //TODO Now change menu item visible will cause show error
//        val mMenuSubForums = menu.findItem(R.id.menu_sub_forums)
//        mMenuSubForums?.isVisible = mListPopupWindow != null

        val newThreadMenu = menu.findItem(R.id.menu_new_thread)
        if (!mUser.isLogged) {
            newThreadMenu.isVisible = false
        }

        val randomImageMenu = menu.findItem(R.id.menu_random_image)
        if (TextUtils.equals(forum.id, "6")) {
            randomImageMenu.isVisible = true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sub_forums -> {
                mListPopupWindow?.anchorView = toolbar.get().findViewById(R.id.menu_sub_forums)
                mListPopupWindow?.show()

                return true
            }
            R.id.menu_new_thread -> {
                NewThreadActivity.startNewThreadActivityForResultMessage(this, Integer.parseInt(forum.id))
                return true
            }
            R.id.menu_random_image -> {
                trackAgent.post(RandomImageTrackEvent())
                GalleryActivity.start(this, Api.randomImage())
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        if (refreshBlackList) {
            showShortSnackbar(R.string.blacklist_refresh_warn)
            refreshBlackList = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PostListActivity.RESULT_BLACKLIST) {
            if (resultCode == Activity.RESULT_OK) {
                refreshBlackList = true
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onPause() {
        refreshBlackList = false
        super.onPause()
    }

    override fun setupSubForums(forumList: List<Forum>) {
        if (mListPopupWindow == null) {
            mListPopupWindow = ListPopupWindow(this)

            mSubForumArrayAdapter = SubForumArrayAdapter(this, R.layout.item_popup_menu_dropdown,
                    forumList)
            mListPopupWindow?.setAdapter(mSubForumArrayAdapter)
            mListPopupWindow?.setOnItemClickListener { parent, view, position, id ->
                // we use the same activity (ThreadListActivity) for sub forum
                ThreadListActivity.startThreadListActivity(this, mSubForumArrayAdapter?.getItem(
                        position))

                mListPopupWindow?.dismiss()
            }

            mListPopupWindow?.setContentWidth(measureContentWidth(mSubForumArrayAdapter))

//            invalidateOptionsMenu()
        } else {
            mSubForumArrayAdapter?.clear()
            mSubForumArrayAdapter?.addAll(forumList)
            mSubForumArrayAdapter?.notifyDataSetChanged()
        }

        // We need to invoke this every times when mSubForumArrayAdapter changes,
        // but now we only invoke this in the first time due to cost-performance.
        // mListPopupWindow.setContentWidth(measureContentWidth(mSubForumArrayAdapter));
    }

    private fun init() {
        mS1Service.getNewThreadInfo(forum.id.toInt())
                .map<List<ThreadType>>(ThreadType::fromXmlString)
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, ActivityEvent.DESTROY))
                .subscribe({
                    threadTypes = ArrayList(it)
                    refreshTabLayout()
                }, {
                    L.report(it)
                })
    }

    /**
     * Forked from android.widget.Spinner#measureContentWidth(SpinnerAdapter, Drawable).
     */
    private fun measureContentWidth(spinnerAdapter: SpinnerAdapter?): Int {
        if (spinnerAdapter == null) {
            return 0
        }

        var width = 0
        var itemView: View? = null
        var itemType = 0
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        // Make sure the number of items we'll measure is capped.
        // If it's a huge data set with wildly varying sizes, oh well.
        var start = 0
        val end = Math.min(spinnerAdapter.count, start + MAX_ITEMS_MEASURED)
        val count = end - start
        start = Math.max(0, start - (MAX_ITEMS_MEASURED - count))
        val parent = toolbar.get()
        for (i in start until end) {
            val positionType = spinnerAdapter.getItemViewType(i)
            if (positionType != itemType) {
                itemType = positionType
                itemView = null
            }
            itemView = spinnerAdapter.getView(i, itemView, parent)
            if (itemView.layoutParams == null) {
                itemView.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            itemView.measure(widthMeasureSpec, heightMeasureSpec)
            width = Math.max(width, itemView.measuredWidth)
        }

        return width
    }

    private fun initTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val type = tab?.tag as ThreadType?
                fragment?.changeTypeId(type?.typeId)
                mRxBus.post(ThreadTypeChangeEvent(type?.typeId ?: "0"))
            }

        })
        refreshTabLayout()
    }

    private fun refreshTabLayout() {
        if (threadTypes?.size ?: 0 <= 1) {
            tabLayout.visibility = View.GONE
        } else {
            tabLayout.removeAllTabs()
            tabLayout.addTab(tabLayout.newTab().setText(R.string.all))
            threadTypes?.forEach { type ->
                if (type.typeId != "0") {
                    type.typeName?.also {
                        tabLayout.addTab(tabLayout.newTab().setText(it).setTag(type))
                    }
                }
            }
            tabLayout.visibility = View.VISIBLE
        }
    }

    companion object {

        private const val ARG_FORUM = "forum"

        /**
         * Only measures this many items to get a decent max width.
         */
        private val MAX_ITEMS_MEASURED = 15

        fun startThreadListActivity(context: Context, forum: Forum?) {
            val intent = Intent(context, ThreadListActivity::class.java)
            intent.putExtra(ARG_FORUM, forum)

            context.startActivity(intent)
        }
    }
}
