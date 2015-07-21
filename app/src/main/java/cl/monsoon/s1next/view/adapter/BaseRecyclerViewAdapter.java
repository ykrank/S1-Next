package cl.monsoon.s1next.view.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    List<T> mList;

    BaseRecyclerViewAdapter() {
        this.mList = Collections.emptyList();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setDataSet(List<T> list) {
        this.mList = list;
    }

    public T getItem(int i) {
        return mList.get(i);
    }
}
