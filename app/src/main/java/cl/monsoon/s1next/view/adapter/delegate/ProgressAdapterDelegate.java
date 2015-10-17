package cl.monsoon.s1next.view.adapter.delegate;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;

import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.view.adapter.item.ProgressItem;

public final class ProgressAdapterDelegate extends AbsAdapterDelegate<List<Object>> {

    private final LayoutInflater mLayoutInflater;

    public ProgressAdapterDelegate(Activity activity, int viewType) {
        super(viewType);

        mLayoutInflater = activity.getLayoutInflater();
    }

    @Override
    public boolean isForViewType(@NonNull List<Object> objects, int i) {
        return objects.get(i) instanceof ProgressItem;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new ProgressViewHolder(mLayoutInflater.inflate(R.layout.item_progress, viewGroup,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull List<Object> objects, int i, @NonNull RecyclerView.ViewHolder viewHolder) {
        // match_parent doesn't work for RecyclerView's item
        ProgressViewHolder holder = (ProgressViewHolder) viewHolder;
        holder.progressBar.setVisibility(View.GONE);
        holder.itemView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);

                ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
                View view = (View) viewHolder.itemView.getParent();
                layoutParams.height = view.getHeight() - view.getPaddingTop() - view.getPaddingBottom();
                holder.progressBar.setVisibility(View.VISIBLE);
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
