package cl.monsoon.s1next.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Favourite;

/**
 * Similar to {@link ForumListRecyclerViewAdapter}.
 */
public final class FavouriteListRecyclerViewAdapter extends BaseRecyclerViewAdapter<Favourite, FavouriteListRecyclerViewAdapter.ViewHolder> {

    public FavouriteListRecyclerViewAdapter() {
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_line_text,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Favourite favourite = mList.get(position);

        TextView textView = holder.textView;
        textView.setText(favourite.getTitle());
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mList.get(position).getId());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView;
        }
    }
}
