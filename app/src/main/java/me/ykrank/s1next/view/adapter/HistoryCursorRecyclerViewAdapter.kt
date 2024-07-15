package me.ykrank.s1next.view.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder;

import me.ykrank.s1next.data.db.biz.HistoryBiz;
import me.ykrank.s1next.databinding.ItemHistoryBinding;
import me.ykrank.s1next.viewmodel.HistoryViewModel;


public final class HistoryCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter<SimpleRecycleViewHolder<ItemHistoryBinding>> {
    private final LayoutInflater mLayoutInflater;

    public HistoryCursorRecyclerViewAdapter(Activity activity) {
        super(activity, null);

        mLayoutInflater = activity.getLayoutInflater();
    }

    @Override
    public SimpleRecycleViewHolder<ItemHistoryBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(mLayoutInflater, parent, false);
        binding.setModel(new HistoryViewModel());
        return new SimpleRecycleViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(SimpleRecycleViewHolder<ItemHistoryBinding> viewHolder, Cursor cursor) {
        ItemHistoryBinding binding = viewHolder.getBinding();
        binding.getModel().history.set(HistoryBiz.Companion.getInstance().fromCursor(cursor));
    }
}
