package cl.monsoon.s1next.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.event.EmoticonClickEvent;
import cl.monsoon.s1next.singleton.BusProvider;

public final class EmoticonGridRecyclerAdapter extends RecyclerView.Adapter<EmoticonGridRecyclerAdapter.ViewHolder> {

    private final List<Pair<String, String>> mEmoticons;
    private final DrawableRequestBuilder<Uri> mEmoticonRequestBuilder;

    public EmoticonGridRecyclerAdapter(Context context, List<Pair<String, String>> emoticons) {
        this.mEmoticons = emoticons;

        setHasStableIds(true);

        mEmoticonRequestBuilder = Glide.with(context).from(Uri.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emoticon, parent,
                false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setTag(R.id.emoticon_entity_tag, mEmoticons.get(position).second);
        mEmoticonRequestBuilder
                .load(Uri.parse(mEmoticons.get(position).first))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mEmoticons.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView;
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // notify ReplyFragment that emoticon had been clicked
            BusProvider.get().post(new EmoticonClickEvent((String) v.getTag(R.id.emoticon_entity_tag)));
        }
    }
}
