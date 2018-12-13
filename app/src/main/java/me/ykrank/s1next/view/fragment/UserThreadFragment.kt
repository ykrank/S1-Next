package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import io.reactivex.Single
import me.ykrank.s1next.data.api.model.HomeThread
import me.ykrank.s1next.data.api.model.wrapper.HomeThreadWebWrapper
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter
import me.ykrank.s1next.view.adapter.HomeThreadRecyclerViewAdapter

/**
 * Created by ykrank on 2017/2/4.
 */

class UserThreadFragment : BaseLoadMoreRecycleViewFragment<HomeThreadWebWrapper>() {

    private var uid: String? = null
    private lateinit var mRecyclerAdapter: HomeThreadRecyclerViewAdapter

    override val isCardViewContainer: Boolean
        get() = true

    override val recyclerViewAdapter: BaseRecyclerViewAdapter
        get() = mRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uid = arguments!!.getString(ARG_UID)
        leavePageMsg("UserThreadFragment")

        val recyclerView = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerAdapter = HomeThreadRecyclerViewAdapter(activity)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun appendNewData(oldData: HomeThreadWebWrapper?, newData: HomeThreadWebWrapper): HomeThreadWebWrapper {
        if (oldData != null) {
            val oldThreads = oldData.threads
            var newThreads: MutableList<HomeThread>? = newData.threads
            if (newThreads == null) {
                newThreads = arrayListOf()
                newData.threads = newThreads
            }
            if (oldThreads != null) {
                newThreads.addAll(0, oldThreads)
            }
        }
        return newData
    }

    override fun getPageSourceObservable(pageNum: Int): Single<HomeThreadWebWrapper> {
        return mS1Service.getHomeThreads(uid, pageNum)
                .map(HomeThreadWebWrapper::fromHtml)
    }

    override fun onNext(data: HomeThreadWebWrapper) {
        super.onNext(data)
        mRecyclerAdapter!!.diffNewDataSet(data.threads, false)
        if (data.isMore) {
            setTotalPages(pageNum + 1)
        } else {
            setTotalPages(pageNum)
        }
    }

    companion object {
        val TAG = UserThreadFragment::class.java.name
        private const val ARG_UID = "uid"

        fun newInstance(uid: String): UserThreadFragment {
            val fragment = UserThreadFragment()
            val bundle = Bundle()
            bundle.putString(ARG_UID, uid)
            fragment.arguments = bundle
            return fragment
        }
    }
}
