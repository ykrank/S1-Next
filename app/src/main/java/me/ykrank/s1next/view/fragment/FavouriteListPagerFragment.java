package me.ykrank.s1next.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import io.reactivex.Observable;
import me.ykrank.s1next.data.api.model.collection.Favourites;
import me.ykrank.s1next.data.api.model.wrapper.BaseResultWrapper;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.MathUtil;
import me.ykrank.s1next.view.adapter.FavouriteRecyclerViewAdapter;
import me.ykrank.s1next.view.internal.PagerCallback;
import me.ykrank.s1next.viewmodel.LoadingViewModel;

/**
 * A Fragment representing one of the pages of favourites.
 * <p>
 * Activity or Fragment containing this must implement {@link PagerCallback}.
 */
public final class FavouriteListPagerFragment extends BaseRecyclerViewFragment<BaseResultWrapper<Favourites>> {

    private static final String ARG_PAGE_NUM = "page_num";

    private int mPageNum;

    private FavouriteRecyclerViewAdapter mRecyclerAdapter;

    private PagerCallback mPagerCallback;

    public static FavouriteListPagerFragment newInstance(int pageNum) {
        FavouriteListPagerFragment fragment = new FavouriteListPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_NUM, pageNum);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mPagerCallback = (PagerCallback) getFragmentManager().findFragmentByTag(
                FavouriteListFragment.Companion.getTAG());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPageNum = getArguments().getInt(ARG_PAGE_NUM);
        L.leaveMsg("FavouriteListPagerFragment##mPageNum" + mPageNum);

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new FavouriteRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mPagerCallback = null;
    }

    @Override
    Observable<BaseResultWrapper<Favourites>> getSourceObservable(@LoadingViewModel.LoadingDef int loading) {
        return mS1Service.getFavouritesWrapper(mPageNum);
    }

    @Override
    void onNext(BaseResultWrapper<Favourites> data) {
        Favourites favourites = data.getData();
        if (favourites.getFavouriteList() == null) {
            consumeResult(data.getResult());
        } else {
            super.onNext(data);

            mRecyclerAdapter.diffNewDataSet(favourites.getFavouriteList(), true);

            // update total page
            mPagerCallback.setTotalPages(MathUtil.divide(favourites.getTotal(),
                    favourites.getFavouritesPerPage()));
        }
    }
}
