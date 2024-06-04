package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.view.View
import com.github.ykrank.androidtools.util.MathUtil
import com.github.ykrank.androidtools.widget.RxBus
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.collection.PmGroups
import me.ykrank.s1next.data.api.model.wrapper.BaseDataWrapper
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter
import me.ykrank.s1next.view.adapter.PmGroupsRecyclerViewAdapter
import me.ykrank.s1next.view.event.NoticeRefreshEvent
import javax.inject.Inject


class PmGroupsFragment : BaseLoadMoreRecycleViewFragment<BaseDataWrapper<PmGroups>>() {
    private lateinit var mRecyclerAdapter: PmGroupsRecyclerViewAdapter

    @Inject
    internal lateinit var mRxBus: RxBus

    override val recyclerViewAdapter: BaseRecyclerViewAdapter
        get() = mRecyclerAdapter

    override val isCardViewContainer: Boolean
        get() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        leavePageMsg("PmGroupsFragment")
        activity?.setTitle(R.string.pms)

        val recyclerView = recyclerView
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        mRecyclerAdapter = PmGroupsRecyclerViewAdapter(activity)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun getPageSourceObservable(pageNum: Int): Single<BaseDataWrapper<PmGroups>> {
        return mS1Service.getPmGroups(pageNum)
    }

    override fun onNext(data: BaseDataWrapper<PmGroups>) {
        super.onNext(data)
        val pmGroups = data.data
        val pmGroupList = pmGroups.list
        if (pmGroupList != null) {
            mRecyclerAdapter.diffNewDataSet(pmGroupList, false)
            // update total page
            setTotalPages(MathUtil.divide(pmGroups.count, pmGroups.perPage))
        }

        if (pageNum == 1) {
            mRxBus.post(NoticeRefreshEvent::class.java, NoticeRefreshEvent(data.data.hasNew(), null))
        }
    }

    override fun appendNewData(oldData: BaseDataWrapper<PmGroups>?, newData: BaseDataWrapper<PmGroups>): BaseDataWrapper<PmGroups> {
        if (oldData != null) {
            val oldPmGroups = oldData.data.list
            var newPmGroups = newData.data.list?.toMutableList()
            if (newPmGroups == null) {
                newPmGroups = ArrayList()
                newData.data.list = newPmGroups
            }
            if (oldPmGroups != null) {
                newPmGroups.addAll(0, oldPmGroups)
            }
        }
        return newData
    }

    companion object {

        val TAG = PmGroupsFragment::class.java.name

        fun newInstance(): PmGroupsFragment {
            return PmGroupsFragment()
        }
    }
}
