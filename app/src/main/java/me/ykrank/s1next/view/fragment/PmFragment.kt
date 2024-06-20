package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.github.ykrank.androidtools.util.MathUtil
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.model.wrapper.PmsWrapper
import me.ykrank.s1next.view.activity.NewPmActivity
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter
import me.ykrank.s1next.view.adapter.PmRecyclerViewAdapter
import javax.inject.Inject

/**
 * Created by ykrank on 2016/11/12 0012.
 */

class PmFragment : BaseLoadMoreRecycleViewFragment<PmsWrapper>() {
    @Inject
    internal lateinit var user: User
    private lateinit var mRecyclerAdapter: PmRecyclerViewAdapter
    private var toUid: String? = null
    private var toUsername: String? = null

    override var dataId: String? = null

    override val recyclerViewAdapter: BaseRecyclerViewAdapter
        get() = mRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            //Random to force valid retained data
            dataId = (Math.random() * Long.MAX_VALUE).toLong().toString()
        } else {
            dataId = savedInstanceState.getString(ARG_DATA_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.appComponent.inject(this)

        toUid = requireArguments().getString(ARG_TO_UID)
        toUsername = requireArguments().getString(ARG_TO_USERNAME)
        leavePageMsg("PmFragment##toUid:$toUid,toUsername$toUsername")
        if (toUid.isNullOrEmpty() || toUsername.isNullOrEmpty()) {
            showShortSnackbar(R.string.message_api_error)
            return
        }
        activity?.title = toUsername

        val recyclerView = recyclerView
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        mRecyclerAdapter = PmRecyclerViewAdapter(activity)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ARG_DATA_ID, dataId)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_pm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_pm -> {
                NewPmActivity.startNewPmActivityForResultMessage(
                    requireActivity(),
                    toUid!!,
                    toUsername!!
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getPageSourceObservable(pageNum: Int): Single<PmsWrapper> {
        return mS1Service.getPmList(toUid, pageNum)
                .map { pmsWrapper -> pmsWrapper.setMsgToUsername(user, toUsername) }
    }

    override fun onNext(data: PmsWrapper) {
        super.onNext(data)
        val pms = data.data
        pms.list?.let {
            // update total page
            val totalPage = MathUtil.divide(pms.count, pms.perPage)

            mRecyclerAdapter.diffNewDataSet(it, false)

            setTotalPages(totalPage)
            //if this is first page and total page > 1, then load more
            if (pageNum < totalPage) {
                startPullUpLoadMore()
            }
        }
    }

    override fun appendNewData(oldData: PmsWrapper?, newData: PmsWrapper): PmsWrapper {
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

        val TAG = PmFragment::class.java.name
        private const val ARG_TO_UID = "to_uid"
        private const val ARG_TO_USERNAME = "to_user_name"
        private const val ARG_DATA_ID = "data_id"

        fun newInstance(toUid: String, toUsername: String): PmFragment {
            val fragment = PmFragment()
            val bundle = Bundle()
            bundle.putString(ARG_TO_UID, toUid)
            bundle.putString(ARG_TO_USERNAME, toUsername)
            fragment.arguments = bundle
            return fragment
        }
    }
}
