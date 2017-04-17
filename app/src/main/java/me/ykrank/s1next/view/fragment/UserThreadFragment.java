package me.ykrank.s1next.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import me.ykrank.s1next.data.api.model.HomeThread;
import me.ykrank.s1next.data.api.model.wrapper.HomeThreadWebWrapper;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter;
import me.ykrank.s1next.view.adapter.HomeThreadRecyclerViewAdapter;

/**
 * Created by ykrank on 2017/2/4.
 */

public class UserThreadFragment extends BaseLoadMoreRecycleViewFragment<HomeThreadWebWrapper> {
    public static final String TAG = UserThreadFragment.class.getName();
    private static final String ARG_UID = "uid";

    private String uid;
    private HomeThreadRecyclerViewAdapter mRecyclerAdapter;

    public static UserThreadFragment newInstance(String uid) {
        UserThreadFragment fragment = new UserThreadFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_UID, uid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uid = getArguments().getString(ARG_UID);
        L.leaveMsg("UserThreadFragment");

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new HomeThreadRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    boolean isCardViewContainer() {
        return true;
    }

    @Override
    BaseRecyclerViewAdapter getRecyclerViewAdapter() {
        return mRecyclerAdapter;
    }

    @NonNull
    @Override
    HomeThreadWebWrapper appendNewData(@Nullable HomeThreadWebWrapper oldData, @NonNull HomeThreadWebWrapper newData) {
        if (oldData != null) {
            List<HomeThread> oldThreads = oldData.getThreads();
            List<HomeThread> newThreads = newData.getThreads();
            if (newThreads == null) {
                newThreads = new ArrayList<>();
            }
            if (oldThreads != null) {
                newThreads.addAll(0, oldThreads);
            }
        }
        return newData;
    }

    @Override
    Observable<HomeThreadWebWrapper> getSourceObservable(int pageNum) {
        return mS1Service.getHomeThreads(uid, pageNum)
                .map(HomeThreadWebWrapper::fromHtml);
    }

    @Override
    void onNext(HomeThreadWebWrapper data) {
        super.onNext(data);
        mRecyclerAdapter.diffNewDataSet(data.getThreads(), false);
        if (data.isMore()) {
            setTotalPages(getPageNum() + 1);
        } else {
            setTotalPages(getPageNum());
        }
    }
}
