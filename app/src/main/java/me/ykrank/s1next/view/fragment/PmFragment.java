package me.ykrank.s1next.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.model.Pm;
import me.ykrank.s1next.data.api.model.collection.Pms;
import me.ykrank.s1next.data.api.model.wrapper.PmsWrapper;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.MathUtil;
import me.ykrank.s1next.view.activity.NewPmActivity;
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter;
import me.ykrank.s1next.view.adapter.PmRecyclerViewAdapter;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;
import rx.Observable;

/**
 * Created by ykrank on 2016/11/12 0012.
 */

public final class PmFragment extends BaseLoadMoreRecycleViewFragment<PmsWrapper> {

    public static final String TAG = PmFragment.class.getName();
    private static final String ARG_TO_UID = "to_uid";
    private static final String ARG_TO_USERNAME = "to_user_name";
    private static final String ARG_DATA_ID = "data_id";
    @Inject
    User user;
    private PmRecyclerViewAdapter mRecyclerAdapter;
    private String toUid;
    private String toUsername;

    private String pmId;

    private String dataId;

    public static PmFragment newInstance(String toUid, String toUsername) {
        PmFragment fragment = new PmFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TO_UID, toUid);
        bundle.putString(ARG_TO_USERNAME, toUsername);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            //Random to force valid retained data
            dataId = String.valueOf(RandomUtils.nextLong());
        } else {
            dataId = savedInstanceState.getString(ARG_DATA_ID);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.getAppComponent(getContext()).inject(this);

        toUid = getArguments().getString(ARG_TO_UID);
        toUsername = getArguments().getString(ARG_TO_USERNAME);
        L.leaveMsg("PmFragment##toUid:" + toUid + ",toUsername" + toUsername);
        if (TextUtils.isEmpty(toUid)) {
            showShortSnackbar(R.string.message_api_error);
            return;
        }

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new PmRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_DATA_ID, dataId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_pm, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_pm:
                NewPmActivity.startNewPmActivityForResultMessage(getActivity(), toUid);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "私信详情-PmFragment"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "私信详情-PmFragment"));
        super.onPause();
    }

    @Override
    BaseRecyclerViewAdapter getRecyclerViewAdapter() {
        return mRecyclerAdapter;
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
            int totalPage = MathUtil.divide(pms.getTotal(), pms.getPmPerPage());
            setTotalPages(totalPage);
            //if this is first page and total page > 1, then load more
            if (getPageNum() == 1 && totalPage > 1) {
                startPullUpLoadMore();
            }
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

    @Override
    public String getDataId() {
        return dataId;
    }
}
