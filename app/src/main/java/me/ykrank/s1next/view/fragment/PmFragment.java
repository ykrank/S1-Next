package me.ykrank.s1next.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.data.api.model.Pm;
import me.ykrank.s1next.data.api.model.collection.Pms;
import me.ykrank.s1next.data.api.model.wrapper.PmWrapper;
import me.ykrank.s1next.util.MathUtil;
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter;
import me.ykrank.s1next.view.adapter.PmRecyclerViewAdapter;
import rx.Observable;

/**
 * Created by ykrank on 2016/11/12 0012.
 */

public final class PmFragment extends BaseLoadMoreRecycleViewFragment<PmWrapper>{

    public static final String TAG = PmFragment.class.getName();
    private PmRecyclerViewAdapter mRecyclerAdapter;

    public static PmFragment newInstance() {
        PmFragment fragment = new PmFragment();
        return fragment;
    }

    @Override
    BaseRecyclerViewAdapter getRecyclerViewAdapter() {
        return mRecyclerAdapter;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new PmRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    Observable<PmWrapper> getSourceObservable(int pageNum) {
        return mS1Service.getPmList(pageNum);
    }

    @Override
    void onNext(PmWrapper data) {
        super.onNext(data);
        Pms pms = data.getPms();
        if (pms.getPmList() != null) {
            mRecyclerAdapter.diffNewDataSet(pms.getPmList(), false);
            // update total page
            setTotalPages(MathUtil.divide(pms.getTotal(), pms.getPmPerPage()));
        }
    }

    @Override
    PmWrapper appendNewData(@Nullable PmWrapper oldData, @NonNull PmWrapper newData) {
        if (oldData != null) {
            List<Pm> oldPms = oldData.getPms().getPmList();
            List<Pm> newPms = newData.getPms().getPmList();
            if (newPms == null) {
                newPms = new ArrayList<>();
            }
            if (oldPms != null) {
                newPms.addAll(0, oldPms);
            }
        }
        return newData;
    }
}
