package me.ykrank.s1next.view.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import com.github.ykrank.androidtools.util.L
import io.reactivex.Single
import io.rx_cache2.DynamicKeyGroup
import io.rx_cache2.EvictDynamicKeyGroup
import me.ykrank.s1next.data.api.model.Forum
import me.ykrank.s1next.data.api.model.wrapper.ThreadsWrapper
import me.ykrank.s1next.util.JsonUtil
import me.ykrank.s1next.view.adapter.ThreadRecyclerViewAdapter
import me.ykrank.s1next.view.fragment.ThreadListPagerFragment.PagerCallback
import me.ykrank.s1next.view.fragment.ThreadListPagerFragment.SubForumsCallback

/**
 * A Fragment representing one of the pages of threads.
 *
 *
 * Activity or Fragment containing this must implement
 * [PagerCallback] and [SubForumsCallback].
 */
class ThreadListPagerFragment : BaseRecyclerViewFragment<ThreadsWrapper>() {

    private var mForumId: String? = null
    private var mPageNum: Int = 0

    private lateinit var mRecyclerAdapter: ThreadRecyclerViewAdapter

    private var mPagerCallback: PagerCallback? = null
    private var mSubForumsCallback: SubForumsCallback? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        mPagerCallback = fragmentManager!!.findFragmentByTag(ThreadListFragment.TAG) as PagerCallback
        mSubForumsCallback = context as SubForumsCallback?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments!!
        mForumId = bundle.getString(ARG_FORUM_ID)
        mPageNum = bundle.getInt(ARG_PAGE_NUM)
        L.leaveMsg("ThreadListPagerFragment##ForumId:$mForumId,PageNum:$mPageNum")

        val recyclerView = recyclerView
        val activity = activity
        recyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerAdapter = ThreadRecyclerViewAdapter(activity, mForumId)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun onDetach() {
        super.onDetach()

        mPagerCallback = null
        mSubForumsCallback = null
    }

    override fun getSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<ThreadsWrapper> {
        val source: Single<String> = if (mDownloadPrefManager.netCacheEnable) {
            apiCacheProvider.getThreadsWrapper(mS1Service.getThreadsWrapper(mForumId, mPageNum),
                    DynamicKeyGroup(mForumId + "," + mPageNum, mUser.key), EvictDynamicKeyGroup(isForceLoading))
        } else {
            mS1Service.getThreadsWrapper(mForumId, mPageNum)
        }
        return source.compose(JsonUtil.jsonSingleTransformer(ThreadsWrapper::class.java))
    }

    override fun onNext(data: ThreadsWrapper) {
        val threads = data.data
        if (threads.threadList.isEmpty()) {
            consumeResult(data.result)
        } else {
            super.onNext(data)

            mRecyclerAdapter.diffNewDataSet(threads.threadList, true)

            // update total page
            mPagerCallback?.setTotalPageByThreads(threads.threadListInfo.threads)

            if (!threads.subForumList.isEmpty()) {
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

        private val ARG_FORUM_ID = "forum_id"
        private val ARG_PAGE_NUM = "page_num"

        fun newInstance(forumId: String, pageNum: Int): ThreadListPagerFragment {
            val fragment = ThreadListPagerFragment()
            val bundle = Bundle()
            bundle.putString(ARG_FORUM_ID, forumId)
            bundle.putInt(ARG_PAGE_NUM, pageNum)
            fragment.arguments = bundle

            return fragment
        }
    }
}
