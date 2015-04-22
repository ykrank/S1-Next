package cl.monsoon.s1next.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Favourite;
import cl.monsoon.s1next.util.ViewUtil;

/**
 * Similar to {@link cl.monsoon.s1next.adapter.ForumListRecyclerAdapter}.
 */
public final class FavouriteListRecyclerAdapter extends RecyclerAdapter<Favourite, FavouriteListRecyclerAdapter.ViewHolder> {

    public FavouriteListRecyclerAdapter() {
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.multi_line_list_item, parent, false);

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
            ViewUtil.updateTextSize(textView);
        }
    }
}
