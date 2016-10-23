package me.ykrank.s1next.view.adapter;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;
import com.hannesdorfmann.adapterdelegates.AdapterDelegate;
import com.hannesdorfmann.adapterdelegates.AdapterDelegatesManager;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.util.Objects;
import me.ykrank.s1next.view.adapter.delegate.ProgressAdapterDelegate;
import me.ykrank.s1next.view.adapter.item.ProgressItem;

public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_PROGRESS = 0;

    private List<Object> mList = new ArrayList<>();
    private final AdapterDelegatesManager<List<Object>> mAdapterDelegatesManager;

    BaseRecyclerViewAdapter(Context context) {
        mAdapterDelegatesManager = new AdapterDelegatesManager<>();
        mAdapterDelegatesManager.addDelegate(new ProgressAdapterDelegate(context,
                VIEW_TYPE_PROGRESS));
    }

    @Override
    @CallSuper
    public int getItemViewType(int position) {
        return mAdapterDelegatesManager.getItemViewType(mList, position);
    }

    @Override
    @CallSuper
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mAdapterDelegatesManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    @CallSuper
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mAdapterDelegatesManager.onBindViewHolder(mList, position, holder);
    }

    @Override
    @CallSuper
    public int getItemCount() {
        return mList.size();
    }

    @Override
    @CallSuper
    public long getItemId(int position) {
        Preconditions.checkArgument(mAdapterDelegatesManager.getItemViewType(mList, position)
                == VIEW_TYPE_PROGRESS);
        return Integer.MIN_VALUE;
    }

    final void addAdapterDelegate(AdapterDelegate<List<Object>> adapterDelegate) {
        Preconditions.checkArgument(adapterDelegate.getItemViewType() != VIEW_TYPE_PROGRESS);
        mAdapterDelegatesManager.addDelegate(adapterDelegate);
    }

    final int getItemViewTypeFromDelegatesManager(int position) {
        return mAdapterDelegatesManager.getItemViewType(mList, position);
    }

    public final void setHasProgress(boolean hasProgress) {
        if (hasProgress) {
            mList.clear();
            mList.add(new ProgressItem());
        } else {
            // we do not need to clear list if we have already changed
            // data set or we have no ProgressItem to been cleared
            if (mList.size() == 1 && mList.get(0) instanceof ProgressItem) {
                mList.clear();
            }
        }
    }

    /**
     * diff new dataSet with old, and dispatch update
     * @see DiffUtil
     * @param newData new data set
     * @param detectMoves DiffUtil.calculateDiff
     */
    public final void refreshDataSet(List<?> newData, boolean detectMoves){
        if (mList == newData){
            throw new IllegalArgumentException("must set new data set");
        }
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new BaseDiffCallback(mList, newData), detectMoves);
        setDataSet(newData);
        diffResult.dispatchUpdatesTo(this);
    }
    
    public final void addDataSet(List<?> list) {
        mList.addAll(list);
    }

    @SuppressWarnings("unchecked")
    public final void setDataSet(List<?> list) {
        mList = (List<Object>) list;
    }

    public final List<Object> getDataSet() {
        return mList;
    }

    final Object getItem(int position) {
        return mList.get(position);
    }

    final void addItem(Object object) {
        mList.add(object);
    }

    final void removeItem(int position) {
        mList.remove(position);
    }

    private static class BaseDiffCallback extends DiffUtil.Callback {
        private List<?> oldData, newData;

        BaseDiffCallback(List<?> oldData, List<?> newData) {
            this.oldData = oldData;
            this.newData = newData;
        }

        @Override
        public int getOldListSize() {
            return oldData.size();
        }

        @Override
        public int getNewListSize() {
            return newData.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldData.get(oldItemPosition), newData.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.hashEquals(oldData.get(oldItemPosition), newData.get(newItemPosition));
        }
    }
}
