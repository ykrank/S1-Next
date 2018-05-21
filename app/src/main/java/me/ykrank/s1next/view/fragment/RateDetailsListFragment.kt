package me.ykrank.s1next.view.fragment

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.ykrank.androidtools.extension.dp2px
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewAdapter
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import io.reactivex.Single
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Rate
import java.util.*

/**
 * Created by ykrank on 2017/1/16.
 */

class RateDetailsListFragment : BaseRecyclerViewFragment<List<Rate>>() {

    private lateinit var rates: List<Rate>
    private lateinit var mRecyclerAdapter: SimpleRecycleViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val argRates = arguments?.getParcelableArrayList<Rate>(ARG_RATES)
        if (argRates != null) {
            rates = argRates
        } else {
            rates = listOf()
        }

        val recyclerView = recyclerView
        val activity = activity
        recyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerAdapter = SimpleRecycleViewAdapter(activity!!, R.layout.item_rate_detail)
        recyclerView.adapter = mRecyclerAdapter
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            val d16 = 16.dp2px(context!!)

            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                outRect?.set(d16, 0, d16, 0)
            }
        })
    }

    override fun getSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<List<Rate>> {
        return Single.just(rates)
    }

    override fun onNext(data: List<Rate>) {
        super.onNext(data)
        mRecyclerAdapter.swapDataSet(data)
    }

    companion object {
        val TAG = RateDetailsListFragment::class.java.name
        private val ARG_RATES = "rates"

        fun instance(rates: ArrayList<Rate>): RateDetailsListFragment {
            val fragment = RateDetailsListFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_RATES, rates)
            fragment.arguments = bundle
            return fragment
        }
    }
}
