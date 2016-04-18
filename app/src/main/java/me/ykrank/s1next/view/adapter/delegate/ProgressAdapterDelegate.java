package me.ykrank.s1next.view.adapter.delegate;

import android.app.Activity;
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

    public ProgressAdapterDelegate(Activity activity, int viewType) {
        super(viewType);

        mLayoutInflater = activity.getLayoutInflater();
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
        // match_parent doesn't work for RecyclerView's item
        ProgressViewHolder progressViewHolder = (ProgressViewHolder) holder;
        progressViewHolder.progressBar.setVisibility(View.GONE);
        progressViewHolder.itemView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);

                ViewGroup.LayoutParams layoutParams = progressViewHolder.itemView.getLayoutParams();
                View view = (View) progressViewHolder.itemView.getParent();
                layoutParams.height = view.getHeight() - view.getPaddingTop() - view.getPaddingBottom();
                progressViewHolder.progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private static final class ProgressViewHolder extends RecyclerView.ViewHolder {

        private final View progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
