package cl.monsoon.s1next.view.adapter;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;
import com.hannesdorfmann.adapterdelegates.AdapterDelegate;
import com.hannesdorfmann.adapterdelegates.AdapterDelegatesManager;

import java.util.ArrayList;
import java.util.List;

import cl.monsoon.s1next.view.adapter.delegate.ProgressAdapterDelegate;
import cl.monsoon.s1next.view.adapter.item.ProgressItem;

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_PROGRESS = 0;

    private final List<Object> mItems;
    private final AdapterDelegatesManager<List<Object>> mAdapterDelegatesManager;

    BaseRecyclerViewAdapter(Activity activity) {
        mItems = new ArrayList<>();
        mAdapterDelegatesManager = new AdapterDelegatesManager<>();
        mAdapterDelegatesManager.addDelegate(new ProgressAdapterDelegate(activity,
                VIEW_TYPE_PROGRESS));
    }

    @Override
    @CallSuper
    public int getItemViewType(int position) {
        return mAdapterDelegatesManager.getItemViewType(mItems, position);
    }

    @Override
    @CallSuper
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mAdapterDelegatesManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    @CallSuper
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mAdapterDelegatesManager.onBindViewHolder(mItems, position, holder);
    }

    @Override
    @CallSuper
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    @CallSuper
    public long getItemId(int position) {
        Preconditions.checkArgument(mAdapterDelegatesManager.getItemViewType(mItems, position)
                == VIEW_TYPE_PROGRESS);
        return Integer.MIN_VALUE;
    }

    final void addAdapterDelegate(AdapterDelegate<List<Object>> adapterDelegate) {
        Preconditions.checkArgument(adapterDelegate.getItemViewType() != VIEW_TYPE_PROGRESS);
        mAdapterDelegatesManager.addDelegate(adapterDelegate);
    }

    final int getItemViewTypeFromDelegatesManager(int position) {
        return mAdapterDelegatesManager.getItemViewType(mItems, position);
    }

    public final void setHasProgress(boolean hasProgress) {
        if (hasProgress) {
            Preconditions.checkState(mItems.size() == 0);
            mItems.add(new ProgressItem());
        } else {
            if (mItems.size() == 1 && mItems.get(0) instanceof ProgressItem) {
                mItems.clear();
            }
        }
    }

    public final void setDataSet(List<T> list) {
        mItems.clear();
        mItems.addAll(list);
    }

    final Object getItem(int position) {
        return mItems.get(position);
    }

    final Object addItem(Object object) {
        return mItems.add(object);
    }

    final Object removeItem(int position) {
        return mItems.remove(position);
    }
}
