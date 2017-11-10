package me.ykrank.s1next.view.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import com.github.ykrank.androidtools.util.L
import io.reactivex.Single
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictDynamicKey
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.model.collection.ForumGroups
import me.ykrank.s1next.data.api.model.wrapper.ForumGroupsWrapper
import me.ykrank.s1next.util.IntentUtil
import me.ykrank.s1next.util.JsonUtil
import me.ykrank.s1next.view.activity.SearchActivity
import me.ykrank.s1next.view.adapter.ForumRecyclerViewAdapter
import me.ykrank.s1next.view.internal.ToolbarDropDownInterface

/**
 * A Fragment represents forum list.
 */
class ForumFragment : BaseRecyclerViewFragment<ForumGroupsWrapper>(), ToolbarDropDownInterface.OnItemSelectedListener {
    private lateinit var mRecyclerAdapter: ForumRecyclerViewAdapter
    private var mForumGroups: ForumGroups? = null

    private var mToolbarCallback: ToolbarDropDownInterface.Callback? = null

    private var inForceRefresh = false

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.leaveMsg("ForumFragment")

        val recyclerView = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerAdapter = ForumRecyclerViewAdapter(activity)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun onAttach(context: Context?) {
        App.appComponent.inject(this)
        super.onAttach(context)

        mToolbarCallback = context as ToolbarDropDownInterface.Callback?
    }

    override fun onDetach() {
        super.onDetach()

        mToolbarCallback = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_forum, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_browser -> {
                IntentUtil.startViewIntentExcludeOurApp(context, Uri.parse(Api.BASE_URL))
                return true
            }
            R.id.app_bar_search -> {
                val activity = activity
                SearchActivity.start(activity, activity.findViewById(R.id.app_bar_search))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun getSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<ForumGroupsWrapper> {
        val source: Single<String> = if (mDownloadPrefManager.netCacheEnable) {
            apiCacheProvider.getForumGroupsWrapper(mS1Service.forumGroupsWrapper, DynamicKey(mUser.key), EvictDynamicKey(isForceLoading))
        } else {
            mS1Service.forumGroupsWrapper
        }
        return source.compose(JsonUtil.jsonSingleTransformer(ForumGroupsWrapper::class.java))
    }

    override fun onNext(data: ForumGroupsWrapper) {
        super.onNext(data)

        mForumGroups = data.data
        // host activity would call #onToolbarDropDownItemSelected(int) after
        mToolbarCallback?.setupToolbarDropDown(mForumGroups?.forumGroupNameList)
    }

    /**
     * Show all forums when `position == 0` otherwise show
     * corresponding forum group's forum list.
     */
    override fun onToolbarDropDownItemSelected(position: Int) {
        mForumGroups?.let {
            if (position == 0) {
                mRecyclerAdapter.refreshDataSet(it.forumList, true)
            } else {
                // the first position is "全部"
                // so position - 1 to correspond its group
                mRecyclerAdapter.refreshDataSet(it.forumGroupList[position - 1], true)
            }
        }
        if (inForceRefresh) {
            inForceRefresh = false
            recyclerView.smoothScrollToPosition(0)
        }
    }

    fun forceSwipeRefresh() {
        inForceRefresh = true
        startSwipeRefresh()
    }

    companion object {
        val TAG = ForumFragment::class.java.name
    }
}
