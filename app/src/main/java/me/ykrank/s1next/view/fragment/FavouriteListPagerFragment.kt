package me.ykrank.s1next.view.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.github.ykrank.androidtools.ui.internal.PagerCallback
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import com.github.ykrank.androidtools.util.MathUtil
import io.reactivex.Single
import me.ykrank.s1next.data.api.model.collection.Favourites
import me.ykrank.s1next.data.api.model.wrapper.BaseResultWrapper
import me.ykrank.s1next.view.adapter.FavouriteRecyclerViewAdapter

/**
 * A Fragment representing one of the pages of favourites.
 *
 *
 * Activity or Fragment containing this must implement [PagerCallback].
 */
class FavouriteListPagerFragment : BaseRecyclerViewFragment<BaseResultWrapper<Favourites>>() {

    private var mPageNum: Int = 0

    private lateinit var mRecyclerAdapter: FavouriteRecyclerViewAdapter

    private var mPagerCallback: PagerCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPagerCallback = parentFragment as PagerCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPageNum = arguments?.getInt(ARG_PAGE_NUM) ?: 0
        leavePageMsg("FavouriteListPagerFragment##mPageNum$mPageNum")

        val recyclerView = recyclerView
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        mRecyclerAdapter = FavouriteRecyclerViewAdapter(activity)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun onDestroy() {
        mPagerCallback = null
        super.onDestroy()
    }

    override fun getSourceObservable(@LoadingViewModel.LoadingDef loading: Int): Single<BaseResultWrapper<Favourites>> {
        return mS1Service.getFavouritesWrapper(mPageNum)
    }

    override fun onNext(data: BaseResultWrapper<Favourites>) {
        val favourites = data.data
        if (favourites.favouriteList == null) {
            consumeResult(data.result)
        } else {
            super.onNext(data)

            mRecyclerAdapter.diffNewDataSet(favourites.favouriteList, true)

            // update total page
            mPagerCallback?.setTotalPages(MathUtil.divide(favourites.total,
                    favourites.favouritesPerPage))
        }
    }

    companion object {

        private const val ARG_PAGE_NUM = "page_num"

        fun newInstance(pageNum: Int): FavouriteListPagerFragment {
            val fragment = FavouriteListPagerFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_PAGE_NUM, pageNum)
            fragment.arguments = bundle

            return fragment
        }
    }
}
