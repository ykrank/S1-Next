package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Single
import me.ykrank.s1next.data.api.model.wrapper.HomeReplyWebWrapper
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter
import me.ykrank.s1next.view.adapter.HomeReplyRecyclerViewAdapter

/**
 * Created by ykrank on 2017/2/4.
 */

class UserReplyFragment : BaseLoadMoreRecycleViewFragment<HomeReplyWebWrapper>() {

    private var uid: String? = null
    private lateinit var mRecyclerAdapter: HomeReplyRecyclerViewAdapter

    override val isCardViewContainer: Boolean
        get() = true

    override val recyclerViewAdapter: BaseRecyclerViewAdapter
        get() = mRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uid = requireArguments().getString(ARG_UID)
        leavePageMsg("UserReplyFragment")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerAdapter = HomeReplyRecyclerViewAdapter(activity)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun appendNewData(oldData: HomeReplyWebWrapper?, newData: HomeReplyWebWrapper): HomeReplyWebWrapper {
        if (oldData != null) {
            val oldReplyItems = oldData.replyItems
            var newReplyItems: MutableList<HomeReplyWebWrapper.HomeReplyItem>? = newData.replyItems
            if (newReplyItems == null) {
                newReplyItems = arrayListOf()
                newData.replyItems = newReplyItems
            }
            if (oldReplyItems != null) {
                newReplyItems.addAll(0, oldReplyItems)
            }
        }
        return newData
    }

    override fun getPageSourceObservable(pageNum: Int): Single<HomeReplyWebWrapper> {
        return mS1Service.getHomeReplies(uid, pageNum)
                .map(HomeReplyWebWrapper::fromHtml)
    }

    override fun onNext(data: HomeReplyWebWrapper) {
        super.onNext(data)
        mRecyclerAdapter.diffNewDataSet(data.replyItems, false)
        if (data.isMore) {
            setTotalPages(pageNum + 1)
        } else {
            setTotalPages(pageNum)
        }
    }

    companion object {
        val TAG = UserReplyFragment::class.java.simpleName
        private const val ARG_UID = "uid"

        fun newInstance(uid: String): UserReplyFragment {
            val fragment = UserReplyFragment()
            val bundle = Bundle()
            bundle.putString(ARG_UID, uid)
            fragment.arguments = bundle
            return fragment
        }
    }
}
