package cl.monsoon.s1next.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.adapter.RecyclerAdapter;
import cl.monsoon.s1next.model.mapper.Deserialization;

public abstract class AbsRecyclerViewFragment<T, D extends Deserialization, VH extends RecyclerView.ViewHolder> extends AbsSwipeRefreshFragment<D> {

    /**
     * {@link RecyclerView} is the main view presenting the data.
     */
    RecyclerView mRecyclerView;
    RecyclerAdapter<T, VH> mRecyclerAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //noinspection ConstantConditions
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initAdapter();
        if (mRecyclerAdapter == null) {
            throw new IllegalStateException("Recycler Adapter can't be null.");
        }
        mRecyclerView.setAdapter((mRecyclerAdapter));
    }

    @Override
    int getLayoutResource() {
        return R.layout.fragment_recycler_view;
    }

    /**
     * Subclass must call {@code super.initAdapter()}
     * and init {@link RecyclerAdapter} properly.
     */
    void initAdapter() {
        if (mRecyclerAdapter != null) {
            throw new IllegalStateException("Mustn't call initAdapter() twice.");
        }
    }
}
