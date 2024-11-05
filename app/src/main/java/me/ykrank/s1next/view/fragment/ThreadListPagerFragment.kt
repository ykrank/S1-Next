package me.ykrank.s1next.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ykrank.androidtools.data.CacheParam
import com.github.ykrank.androidtools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.ykrank.s1next.App
import me.ykrank.s1next.data.api.model.Forum
import me.ykrank.s1next.data.api.model.wrapper.ThreadsWrapper
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.view.adapter.ThreadRecyclerViewAdapter
import me.ykrank.s1next.view.event.PostDisableStickyChangeEvent
import me.ykrank.s1next.view.event.ThreadTypeChangeEvent
import me.ykrank.s1next.view.fragment.ThreadListPagerFragment.PagerCallback
import me.ykrank.s1next.view.fragment.ThreadListPagerFragment.SubForumsCallback
import javax.inject.Inject

/**
 * A Fragment representing one of the pages of threads.
 *
 *
 * Activity or Fragment containing this must implement
 * [PagerCallback] and [SubForumsCallback].
 */
class ThreadListPagerFragment : BaseRecyclerViewFragment<ThreadsWrapper>() {

    @Inject
    internal lateinit var mGeneralPreferencesManager: GeneralPreferencesManager

    private var mForumId: String? = null
    private var mTypeId: String? = null
    private var mPageNum: Int = 0

    private var mRecyclerAdapter: ThreadRecyclerViewAdapter? = null

    private var mPagerCallback: PagerCallback? = null
    private var mSubForumsCallback: SubForumsCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mSubForumsCallback = context as SubForumsCallback?
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)

        mPagerCallback = parentFragment as PagerCallback
        val bundle = requireArguments()
        mForumId = bundle.getString(ARG_FORUM_ID)
        mTypeId = bundle.getString(ARG_TYPE_ID)
        mPageNum = bundle.getInt(ARG_PAGE_NUM)
        leavePageMsg("ThreadListPagerFragment##ForumId:$mForumId, TypeId:$mTypeId, PageNum:$mPageNum")

        lifecycleScope.launch {
            mEventBus.getClsFlow<ThreadTypeChangeEvent>()
                .collect {
                    if (mTypeId != it.typeId) {
                        mTypeId = it.typeId
                        startSwipeRefresh()
                    }
                }
            mEventBus.getClsFlow<PostDisableStickyChangeEvent>()
                .collect {
                    startSwipeRefresh()
                }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRecyclerAdapter = ThreadRecyclerViewAdapter(requireActivity(), viewLifecycleOwner, mForumId)
        val recyclerView = recyclerView
        val activity = requireActivity()
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun onDestroy() {
        mPagerCallback = null
        super.onDestroy()
    }

    override fun onDetach() {
        mSubForumsCallback = null
        super.onDetach()
    }

    override suspend fun getSource(loading: Int): Flow<Resource<ThreadsWrapper>>? {
        val source = apiCacheProvider.getThreadsWrapper(
            mForumId?:"", mTypeId, mPageNum,
            CacheParam(isIgnoreCache)
        )
        if (mGeneralPreferencesManager.isPostDisableSticky) {
            return source.map {
                it.data?.data?.threadList?.apply {
                    val list = filter { it.displayOrder == 0 }
                    clear()
                    addAll(list)
                }
                it
            }
        }
        return source
    }

    override fun onNext(data: ThreadsWrapper) {
        val threads = data.data
        if (threads == null || threads.threadList.isEmpty()) {
            consumeResult(data.result)
        } else {
            super.onNext(data)

            mRecyclerAdapter?.diffNewDataSet(threads.threadList, true)

            // update total page
            mPagerCallback?.setTotalPageByThreads(threads.threadListInfo?.threads ?: 0)

            if (threads.subForumList.isNotEmpty()) {
                mSubForumsCallback?.setupSubForums(threads.subForumList)
            }
        }
    }

    interface PagerCallback {

        /**
         * A callback to set actual total pages
         * which used for [android.support.v4.view.PagerAdapter]。
         */
        fun setTotalPageByThreads(threads: Int)
    }

    interface SubForumsCallback {

        fun setupSubForums(forumList: List<Forum>)
    }

    companion object {

        private const val ARG_FORUM_ID = "forum_id"
        private const val ARG_TYPE_ID = "type_id"
        private const val ARG_PAGE_NUM = "page_num"

        fun newInstance(forumId: String, typeId: String, pageNum: Int): ThreadListPagerFragment {
            val fragment = ThreadListPagerFragment()
            val bundle = Bundle()
            bundle.putString(ARG_FORUM_ID, forumId)
            bundle.putString(ARG_TYPE_ID, typeId)
            bundle.putInt(ARG_PAGE_NUM, pageNum)
            fragment.arguments = bundle

            return fragment
        }
    }
}
