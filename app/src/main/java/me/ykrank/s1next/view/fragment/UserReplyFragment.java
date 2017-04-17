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
import me.ykrank.s1next.data.api.model.wrapper.HomeReplyWebWrapper;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter;
import me.ykrank.s1next.view.adapter.HomeReplyRecyclerViewAdapter;

/**
 * Created by ykrank on 2017/2/4.
 */

public class UserReplyFragment extends BaseLoadMoreRecycleViewFragment<HomeReplyWebWrapper> {
    public static final String TAG = UserReplyFragment.class.getName();
    private static final String ARG_UID = "uid";

    private String uid;
    private HomeReplyRecyclerViewAdapter mRecyclerAdapter;

    public static UserReplyFragment newInstance(String uid) {
        UserReplyFragment fragment = new UserReplyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_UID, uid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uid = getArguments().getString(ARG_UID);
        L.leaveMsg("UserReplyFragment");

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new HomeReplyRecyclerViewAdapter(getActivity());
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
    HomeReplyWebWrapper appendNewData(@Nullable HomeReplyWebWrapper oldData, @NonNull HomeReplyWebWrapper newData) {
        if (oldData != null) {
            List<HomeReplyWebWrapper.HomeReplyItem> oldReplyItems = oldData.getReplyItems();
            List<HomeReplyWebWrapper.HomeReplyItem> newReplyItems = newData.getReplyItems();
            if (newReplyItems == null) {
                newReplyItems = new ArrayList<>();
            }
            if (oldReplyItems != null) {
                newReplyItems.addAll(0, oldReplyItems);
            }
        }
        return newData;
    }

    @Override
    Observable<HomeReplyWebWrapper> getSourceObservable(int pageNum) {
        return mS1Service.getHomeReplies(uid, pageNum)
                .map(HomeReplyWebWrapper::fromHtml);
    }

    @Override
    void onNext(HomeReplyWebWrapper data) {
        super.onNext(data);
        mRecyclerAdapter.diffNewDataSet(data.getReplyItems(), false);
        if (data.isMore()) {
            setTotalPages(getPageNum() + 1);
        } else {
            setTotalPages(getPageNum());
        }
    }
}
