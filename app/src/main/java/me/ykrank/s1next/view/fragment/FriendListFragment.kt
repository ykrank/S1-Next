package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import com.github.ykrank.androidtools.util.L
import io.reactivex.Single
import me.ykrank.s1next.data.api.model.collection.Friends
import me.ykrank.s1next.data.api.model.wrapper.BaseDataWrapper
import me.ykrank.s1next.view.adapter.FriendRecyclerViewAdapter

/**
 * Created by ykrank on 2017/1/16.
 */

class FriendListFragment : BaseRecyclerViewFragment<BaseDataWrapper<Friends>>() {

    private var uid: String? = null
    private lateinit var mRecyclerAdapter: FriendRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uid = arguments?.getString(ARG_UID)
        L.leaveMsg("FriendListFragment##Uid:$uid")

        val recyclerView = recyclerView
        val activity = activity
        recyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerAdapter = FriendRecyclerViewAdapter(activity)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun getSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<BaseDataWrapper<Friends>> {
        return mS1Service.getFriends(uid)
    }

    override fun onNext(data: BaseDataWrapper<Friends>) {
        val friends = data.data?.friendList
        if (friends.isNullOrEmpty()) {
            //No data
        } else {
            super.onNext(data)
            mRecyclerAdapter.diffNewDataSet(friends, true)
        }
    }

    companion object {
        val TAG = FriendListFragment::class.java.name
        private val ARG_UID = "uid"

        fun newInstance(uid: String): FriendListFragment {
            val fragment = FriendListFragment()
            val bundle = Bundle()
            bundle.putString(ARG_UID, uid)
            fragment.arguments = bundle
            return fragment
        }
    }
}
