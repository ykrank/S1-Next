package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.view.View
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.model.darkroom.DarkRoomWrapper
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter
import me.ykrank.s1next.view.adapter.DarkRoomRecyclerViewAdapter
import javax.inject.Inject

/**
 * Created by ykrank on 2016/11/12 0012.
 */

class DarkRoomFragment : BaseLoadMoreRecycleViewFragment<DarkRoomWrapper>() {
    @Inject
    internal lateinit var user: User
    private lateinit var mRecyclerAdapter: DarkRoomRecyclerViewAdapter

    private var lastCid = ""

    override val recyclerViewAdapter: BaseRecyclerViewAdapter
        get() = mRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        leavePageMsg("DarkRoomFragment")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = recyclerView
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        mRecyclerAdapter = DarkRoomRecyclerViewAdapter(activity)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun getPageSourceObservable(pageNum: Int): Single<DarkRoomWrapper> {
        if (pageNum <= 1) {
            lastCid = ""
        }
        return mS1Service.getDarkRoom(lastCid)
    }

    override fun onNext(data: DarkRoomWrapper) {
        super.onNext(data)

        lastCid = data.message?.cid ?: ""
        mRecyclerAdapter.diffNewDataSet(data.darkRooms, false)

        if (data.last) {
            setTotalPages(pageNum)
        } else {
            setTotalPages(pageNum + 1)
        }
    }

    override fun appendNewData(oldData: DarkRoomWrapper?, newData: DarkRoomWrapper): DarkRoomWrapper {
        if (oldData != null) {
            val olds = oldData.darkRooms
            val news = newData.darkRooms.toMutableList()

            if (news.isEmpty()) {
                newData.last = true
            }
            news.addAll(0, olds)
            newData.darkRooms = news
        }
        return newData
    }

    companion object {

        val TAG = DarkRoomFragment::class.java.name

        fun newInstance(): DarkRoomFragment {
            return DarkRoomFragment()
        }
    }
}
