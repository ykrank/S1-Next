package me.ykrank.s1next.view.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.github.ykrank.androidtools.data.CacheParam
import com.github.ykrank.androidtools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.model.collection.ForumGroups
import me.ykrank.s1next.data.api.model.wrapper.ForumGroupsWrapper
import me.ykrank.s1next.util.IntentUtil
import me.ykrank.s1next.view.activity.SearchActivity
import me.ykrank.s1next.view.adapter.ForumRecyclerViewAdapter
import me.ykrank.s1next.view.event.LoginEvent
import me.ykrank.s1next.view.internal.ToolbarDropDownInterface

/**
 * A Fragment represents forum list.
 */
class ForumFragment : BaseRecyclerViewFragment<ForumGroupsWrapper>(),
    ToolbarDropDownInterface.OnItemSelectedListener {
    private lateinit var mRecyclerAdapter: ForumRecyclerViewAdapter
    private var mForumGroups: ForumGroups? = null

    private var mToolbarCallback: ToolbarDropDownInterface.Callback? = null

    private var inForceRefresh = false

    override fun onAttach(context: Context) {
        App.appComponent.inject(this)
        super.onAttach(context)

        mToolbarCallback = context as ToolbarDropDownInterface.Callback?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        leavePageMsg("ForumFragment")

        val recyclerView = recyclerView
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        mRecyclerAdapter = ForumRecyclerViewAdapter(activity)
        recyclerView.adapter = mRecyclerAdapter

        lifecycleScope.launch {
            mEventBus.getClsFlow<LoginEvent>()
                .collect {
                    forceSwipeRefresh()
                }
        }
    }

    override fun onDetach() {
        super.onDetach()

        mToolbarCallback = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_forum, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_browser -> {
                IntentUtil.startViewIntentExcludeOurApp(requireContext(), Uri.parse(Api.BASE_URL))
                true
            }

            R.id.app_bar_search -> {
                val activity = requireActivity()
                SearchActivity.start(activity, activity.findViewById(R.id.app_bar_search))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override suspend fun getSource(loading: Int): Flow<Resource<ForumGroupsWrapper>> {
        return apiCacheProvider.getForumGroupsWrapper(CacheParam(isForceLoading))
    }

    override fun onNext(data: ForumGroupsWrapper) {
        super.onNext(data)

        mForumGroups = data.data
        // host activity would call #onToolbarDropDownItemSelected(int) after
        mToolbarCallback?.setupToolbarDropDown(mForumGroups?.forumGroupNameList ?: emptyList())
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
                mRecyclerAdapter.refreshDataSet(
                    it.forumGroupList.getOrNull(position - 1) ?: emptyList(), true
                )
            }
        }
        if (inForceRefresh) {
            inForceRefresh = false

            recyclerView.post {
                recyclerView.smoothScrollToPosition(0)
            }
        }
    }

    private fun forceSwipeRefresh() {
        inForceRefresh = true
        startSwipeRefresh()
    }

    companion object {
        val TAG = ForumFragment::class.java.simpleName
    }
}
