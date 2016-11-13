package me.ykrank.s1next.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.model.Pm;
import me.ykrank.s1next.data.api.model.collection.Pms;
import me.ykrank.s1next.data.api.model.wrapper.PmsWrapper;
import me.ykrank.s1next.util.MathUtil;
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter;
import me.ykrank.s1next.view.adapter.PmRecyclerViewAdapter;
import rx.Observable;

/**
 * Created by ykrank on 2016/11/12 0012.
 */

public final class PmFragment extends BaseLoadMoreRecycleViewFragment<PmsWrapper>{

    public static final String TAG = PmFragment.class.getName();
    private static final String ARG_TO_UID = "to_uid";
    private static final String ARG_TO_USERNAME = "to_user_name";

    private PmRecyclerViewAdapter mRecyclerAdapter;

    @Inject
    User user;

    private String toUid;
    private String toUsername;

    public static PmFragment newInstance(String toUid, String toUsername) {
        PmFragment fragment = new PmFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TO_UID, toUid);
        bundle.putString(ARG_TO_USERNAME, toUsername);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    BaseRecyclerViewAdapter getRecyclerViewAdapter() {
        return mRecyclerAdapter;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.getAppComponent(getContext()).inject(this);

        toUid = getArguments().getString(ARG_TO_UID);
        toUsername = getArguments().getString(ARG_TO_USERNAME);
        if (TextUtils.isEmpty(toUid)){
            showShortSnackbar(R.string.message_api_error);
            return;
        }

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new PmRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    Observable<PmsWrapper> getSourceObservable(int pageNum) {
        return mS1Service.getPmList(toUid, pageNum)
                .map(pmsWrapper -> pmsWrapper.setMsgToUsername(user, toUsername));
    }

    @Override
    void onNext(PmsWrapper data) {
        super.onNext(data);
        Pms pms = data.getPms();
        if (pms.getPmList() != null) {
            mRecyclerAdapter.diffNewDataSet(pms.getPmList(), false);
            // update total page
            setTotalPages(MathUtil.divide(pms.getTotal(), pms.getPmPerPage()));
        }
    }

    @Override
    PmsWrapper appendNewData(@Nullable PmsWrapper oldData, @NonNull PmsWrapper newData) {
        if (oldData != null) {
            List<Pm> oldPmGroups = oldData.getPms().getPmList();
            List<Pm> newPmGroups = newData.getPms().getPmList();
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
