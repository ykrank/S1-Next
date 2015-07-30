package cl.monsoon.s1next.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import cl.monsoon.s1next.data.api.model.collection.Favourites;
import cl.monsoon.s1next.data.api.model.wrapper.FavouritesWrapper;
import cl.monsoon.s1next.util.MathUtil;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.view.adapter.FavouriteListRecyclerViewAdapter;
import cl.monsoon.s1next.view.internal.PagerCallback;
import rx.Observable;

/**
 * A Fragment representing one of the pages of favourites.
 * <p>
 * Activity or Fragment containing this must implement {@link PagerCallback}.
 */
public final class FavouriteListPagerFragment extends BaseFragment<FavouritesWrapper> {

    private static final String ARG_PAGE_NUM = "page_num";

    private int mPageNum;

    private FavouriteListRecyclerViewAdapter mRecyclerAdapter;

    private PagerCallback mPagerCallback;

    public static FavouriteListPagerFragment newInstance(int pageNum) {
        FavouriteListPagerFragment fragment = new FavouriteListPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_NUM, pageNum);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mPagerCallback = (PagerCallback) getFragmentManager().findFragmentByTag(
                FavouriteListFragment.TAG);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPageNum = getArguments().getInt(ARG_PAGE_NUM);

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new FavouriteListRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mPagerCallback = null;
    }

    @Override
    Observable<FavouritesWrapper> getSourceObservable() {
        return mS1Service.getFavouritesWrapper(mPageNum);
    }

    @Override
    void onNext(FavouritesWrapper data) {
        super.onNext(data);

        Favourites favourites = data.getFavourites();
        if (favourites.getFavouriteList() == null) {
            String message = data.getResult().getMessage();
            // if user has logged out
            if (!TextUtils.isEmpty(message)) {
                ToastUtil.showByText(message, Toast.LENGTH_SHORT);
            }
        } else {
            mRecyclerAdapter.setDataSet(favourites.getFavouriteList());
            mRecyclerAdapter.notifyDataSetChanged();

            // update total page
            mPagerCallback.setTotalPage(MathUtil.divide(favourites.getTotal(),
                    favourites.getFavouritesPerPage()));
        }
    }

    @Override
    void onError(Throwable throwable) {
        if (getUserVisibleHint()) {
            super.onError(throwable);
        }
    }
}
