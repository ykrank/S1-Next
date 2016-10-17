package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;

import java.util.List;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.adapter.item.ProgressItem;

public final class ProgressAdapterDelegate extends AbsAdapterDelegate<List<Object>> {

    private final LayoutInflater mLayoutInflater;

    public ProgressAdapterDelegate(Context context, int viewType) {
        super(viewType);

        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public boolean isForViewType(@NonNull List<Object> items, int position) {
        return items.get(position) instanceof ProgressItem;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ProgressViewHolder(mLayoutInflater.inflate(R.layout.item_progress, parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull List<Object> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        ProgressViewHolder progressViewHolder = (ProgressViewHolder) holder;
        progressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private static final class ProgressViewHolder extends RecyclerView.ViewHolder {

        private final View progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
