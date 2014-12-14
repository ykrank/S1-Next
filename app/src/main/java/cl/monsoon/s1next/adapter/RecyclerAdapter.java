package cl.monsoon.s1next.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public abstract class RecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    List<T> mList;

    RecyclerAdapter() {
        this.mList = Collections.emptyList();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setDataSet(List<T> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        int start = this.mList.size();
        this.mList.addAll(list);
        notifyItemRangeChanged(start, list.size());
    }

    public T getItem(int i) {
        return mList.get(i);
    }
}
