package cl.monsoon.s1next.view.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> mList;

    BaseRecyclerViewAdapter() {
        this.mList = Collections.emptyList();
    }

    public void setDataSet(List<T> list) {
        this.mList = list;
    }

    void addItem(T t) {
        mList.add(t);
    }

    T getItem(int i) {
        return mList.get(i);
    }

    T removeItem(int position) {
        return mList.remove(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
