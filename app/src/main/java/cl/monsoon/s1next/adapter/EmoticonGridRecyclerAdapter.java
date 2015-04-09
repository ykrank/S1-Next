package cl.monsoon.s1next.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.Map;

import cl.monsoon.s1next.Config;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.event.EmoticonClickEvent;
import cl.monsoon.s1next.singleton.BusProvider;

public final class EmoticonGridRecyclerAdapter extends RecyclerView.Adapter<EmoticonGridRecyclerAdapter.ViewHolder> {

    private static final int KEY_EMOTICON_ENTITY = Integer.MIN_VALUE;

    private final LinkedMap<String, String> mEmoticonMap;
    private final DrawableRequestBuilder<Uri> mEmoticonRequestBuilder;

    public EmoticonGridRecyclerAdapter(Context context, Map<String, String> emoticonMap) {
        this.mEmoticonMap = new LinkedMap<>(emoticonMap);

        setHasStableIds(true);

        mEmoticonRequestBuilder = Glide.with(context).from(Uri.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.emoticon, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setTag(KEY_EMOTICON_ENTITY, mEmoticonMap.getValue(position));
        mEmoticonRequestBuilder
                .load(Uri.parse(Config.PREFIX_EMOTICON_ASSET + mEmoticonMap.get(position)))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mEmoticonMap.size();
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
            BusProvider.get().post(new EmoticonClickEvent((String) v.getTag(KEY_EMOTICON_ENTITY)));
        }
    }
}
