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
import me.ykrank.s1next.data.api.model.Note;
import me.ykrank.s1next.data.api.model.collection.Notes;
import me.ykrank.s1next.data.api.model.wrapper.BaseDataWrapper;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.MathUtil;
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter;
import me.ykrank.s1next.view.adapter.NoteRecyclerViewAdapter;
import me.ykrank.s1next.view.event.NoticeRefreshEvent;
import me.ykrank.s1next.widget.EventBus;

/**
 * Created by ykrank on 2017/1/5.
 */

public class NoteFragment extends BaseLoadMoreRecycleViewFragment<BaseDataWrapper<Notes>> {
    public static final String TAG = NoteFragment.class.getName();
    private NoteRecyclerViewAdapter mRecyclerAdapter;

    @Inject
    EventBus mEventBus;

    public static NoteFragment newInstance() {
        NoteFragment fragment = new NoteFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onViewCreated(view, savedInstanceState);
        L.leaveMsg("NoteFragment");

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new NoteRecyclerViewAdapter(getActivity());
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
    BaseDataWrapper<Notes> appendNewData(@Nullable BaseDataWrapper<Notes> oldData, @NonNull BaseDataWrapper<Notes> newData) {
        if (oldData != null) {
            List<Note> oldNotes = oldData.getData().getNoteList();
            List<Note> newNotes = newData.getData().getNoteList();
            if (newNotes == null) {
                newNotes = new ArrayList<>();
            }
            if (oldNotes != null) {
                newNotes.addAll(0, oldNotes);
            }
        }
        return newData;
    }

    @Override
    Observable<BaseDataWrapper<Notes>> getPageSourceObservable(int pageNum) {
        return mS1Service.getMyNotes(pageNum);
    }

    @Override
    void onNext(BaseDataWrapper<Notes> data) {
        super.onNext(data);
        Notes notes = data.getData();
        if (notes != null && notes.getNoteList() != null) {
            mRecyclerAdapter.diffNewDataSet(notes.getNoteList(), false);
            //update total page
            setTotalPages(MathUtil.divide(notes.getCount(), notes.getPerPage()));
        }

        if (getPageNum() == 1) {
            mEventBus.post(NoticeRefreshEvent.class, new NoticeRefreshEvent(null, false));
        }
    }
}
