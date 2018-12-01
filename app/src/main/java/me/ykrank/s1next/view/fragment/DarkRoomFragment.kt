package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.ykrank.androidtools.util.L
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

    override val recyclerViewAdapter: BaseRecyclerViewAdapter
        get() = mRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.appComponent.inject(this)

        L.leaveMsg("DarkRoomFragment")

        val recyclerView = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerAdapter = DarkRoomRecyclerViewAdapter(activity)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun getPageSourceObservable(pageNum: Int): Single<DarkRoomWrapper> {
        return mS1Service.getDarkRoom("")
    }

    override fun onNext(data: DarkRoomWrapper) {
        super.onNext(data)
        val wrapper = data.data
    }

    override fun appendNewData(oldData: DarkRoomWrapper?, newData: DarkRoomWrapper): DarkRoomWrapper {
        if (oldData != null) {
            val oldPmGroups = oldData.data
            var newPmGroups = newData.data
            if (newPmGroups == null) {
                newPmGroups = hashMapOf()
            }
            if (oldPmGroups != null) {
                newPmGroups.putAll(oldPmGroups)
            }
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
