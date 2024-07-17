package me.ykrank.s1next.view.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import com.github.ykrank.androidtools.extension.dp2px
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewAdapter
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import io.reactivex.Single
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Rate

/**
 * Created by ykrank on 2017/1/16.
 */

class RateDetailsListFragment : BaseRecyclerViewFragment<List<Rate>>() {

    private lateinit var rates: List<Rate>
    private lateinit var mRecyclerAdapter: SimpleRecycleViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val argRates = arguments?.getParcelableArrayList<Rate>(ARG_RATES)
        if (argRates != null) {
            rates = argRates
        } else {
            rates = listOf()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = recyclerView
        val activity = activity
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        mRecyclerAdapter = SimpleRecycleViewAdapter(activity!!, R.layout.item_rate_detail_multi, true)
        recyclerView.adapter = mRecyclerAdapter
        recyclerView.addItemDecoration(object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
            val d16 = 16.dp2px(context!!)

            override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
                outRect.set(d16, 0, d16, 0)
            }
        })
    }

    override fun getSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<List<Rate>> {
        return Single.just(rates)
    }

    override fun onNext(data: List<Rate>) {
        super.onNext(data)
        mRecyclerAdapter.diffNewDataSet(data, true)
    }

    companion object {
        val TAG = RateDetailsListFragment::class.java.name
        private const val ARG_RATES = "rates"

        fun instance(rates: ArrayList<Rate>): RateDetailsListFragment {
            val fragment = RateDetailsListFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_RATES, rates)
            fragment.arguments = bundle
            return fragment
        }
    }
}
