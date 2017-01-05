package me.ykrank.s1next.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import me.ykrank.s1next.data.api.model.Note;
import me.ykrank.s1next.data.api.model.collection.Notes;
import me.ykrank.s1next.data.api.model.wrapper.NotesWrapper;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter;
import me.ykrank.s1next.view.adapter.NoteRecyclerViewAdapter;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;
import rx.Observable;

/**
 * Created by ykrank on 2017/1/5.
 */

public class NoteFragment extends BaseLoadMoreRecycleViewFragment<NotesWrapper> {
    public static final String TAG = NoteFragment.class.getName();
    private NoteRecyclerViewAdapter mRecyclerAdapter;

    public static NoteFragment newInstance() {
        NoteFragment fragment = new NoteFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        L.leaveMsg("NoteFragment");

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new NoteRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "消息列表-NoteFragment"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "消息列表-NoteFragment"));
        super.onPause();
    }

    @Override
    BaseRecyclerViewAdapter getRecyclerViewAdapter() {
        return mRecyclerAdapter;
    }

    @Override
    NotesWrapper appendNewData(@Nullable NotesWrapper oldData, @NonNull NotesWrapper newData) {
        if (oldData != null) {
            Map<Integer, Note> oldNotes = oldData.getNotes().getDatas();
            Map<Integer, Note> newNotes = newData.getNotes().getDatas();
            if (newNotes == null) {
                newNotes = new HashMap<>();
            }
            if (oldNotes != null) {
                newNotes.putAll(oldNotes);
            }
        }
        return newData;
    }

    @Override
    Observable<NotesWrapper> getSourceObservable(int pageNum) {
        return mS1Service.getMyNotes(pageNum);
    }

    @Override
    void onNext(NotesWrapper data) {
        super.onNext(data);
        Notes notes = data.getNotes();
        if (notes != null && notes.getDatas() != null) {
//            mRecyclerAdapter.diffNewDataSet(notes.getDatas().values(), false);
            // update total page
//            setTotalPages(MathUtil.divide(pmGroups.getTotal(), pmGroups.getPmPerPage()));
        }
    }
}
