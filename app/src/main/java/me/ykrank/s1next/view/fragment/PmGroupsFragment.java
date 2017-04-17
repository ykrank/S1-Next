package me.ykrank.s1next.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.PmGroup;
import me.ykrank.s1next.data.api.model.collection.PmGroups;
import me.ykrank.s1next.data.api.model.wrapper.BaseDataWrapper;
import me.ykrank.s1next.data.event.NoticeRefreshEvent;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.MathUtil;
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter;
import me.ykrank.s1next.view.adapter.PmGroupsRecyclerViewAdapter;
import me.ykrank.s1next.widget.EventBus;


public final class PmGroupsFragment extends BaseLoadMoreRecycleViewFragment<BaseDataWrapper<PmGroups>> {

    public static final String TAG = PmGroupsFragment.class.getName();
    private PmGroupsRecyclerViewAdapter mRecyclerAdapter;

    @Inject
    EventBus mEventBus;

    public static PmGroupsFragment newInstance() {
        PmGroupsFragment fragment = new PmGroupsFragment();
        return fragment;
    }

    @Override
    BaseRecyclerViewAdapter getRecyclerViewAdapter() {
        return mRecyclerAdapter;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onViewCreated(view, savedInstanceState);
        L.leaveMsg("PmGroupsFragment");
        getActivity().setTitle(R.string.pms);

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new PmGroupsRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    boolean isCardViewContainer() {
        return true;
    }

    @Override
    Observable<BaseDataWrapper<PmGroups>> getSourceObservable(int pageNum) {
        return mS1Service.getPmGroups(pageNum);
    }

    @Override
    void onNext(BaseDataWrapper<PmGroups> data) {
        super.onNext(data);
        PmGroups pmGroups = data.getData();
        if (pmGroups.getPmGroupList() != null) {
            mRecyclerAdapter.diffNewDataSet(pmGroups.getPmGroupList(), false);
            // update total page
            setTotalPages(MathUtil.divide(pmGroups.getTotal(), pmGroups.getPmPerPage()));
        }

        if (getPageNum() == 1) {
            mEventBus.post(new NoticeRefreshEvent(data.getData().hasNew(), false));
        }
    }

    @NonNull
    @Override
    BaseDataWrapper<PmGroups> appendNewData(@Nullable BaseDataWrapper<PmGroups> oldData, @NonNull BaseDataWrapper<PmGroups> newData) {
        if (oldData != null) {
            List<PmGroup> oldPmGroups = oldData.getData().getPmGroupList();
            List<PmGroup> newPmGroups = newData.getData().getPmGroupList();
            if (newPmGroups == null) {
                newPmGroups = new ArrayList<>();
            }
            if (oldPmGroups != null) {
                newPmGroups.addAll(0, oldPmGroups);
            }
        }
        return newData;
    }
}
