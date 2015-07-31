package cl.monsoon.s1next.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.common.base.Preconditions;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.Api;
import cl.monsoon.s1next.data.api.model.Post;
import cl.monsoon.s1next.data.event.QuoteEvent;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.databinding.ItemPostBinding;
import cl.monsoon.s1next.widget.GlideImageGetter;
import cl.monsoon.s1next.widget.TagHandler;

/**
 * This {@link android.support.v7.widget.RecyclerView.Adapter}
 * has another item type {@link #TYPE_FOOTER_PROGRESS}
 * in order to implement pull up to refresh.
 */
public final class PostListRecyclerViewAdapter extends BaseRecyclerViewAdapter<Post, RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER_PROGRESS = Integer.MIN_VALUE;

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;

    private final DownloadPreferencesManager mDownloadPreferencesManager;

    private final DrawableRequestBuilder<String> mAvatarRequestBuilder;

    public PostListRecyclerViewAdapter(Activity activity) {
        mContext = activity;
        mLayoutInflater = activity.getLayoutInflater();
        mDownloadPreferencesManager = App.getAppComponent(mContext).getDownloadPreferencesManager();

        setHasStableIds(true);

        // loading avatars is prior to images in replies
        mAvatarRequestBuilder = Glide.with(mContext)
                .from(String.class)
                .error(R.drawable.ic_avatar_placeholder)
                .priority(Priority.HIGH)
                .transform(new CenterCrop(Glide.get(mContext).getBitmapPool()));
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterProgress(position)) {
            return TYPE_FOOTER_PROGRESS;
        }

        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER_PROGRESS) {
            return new FooterProgressViewHolder(mLayoutInflater.inflate(
                    R.layout.item_post_footer_progress, parent, false));
        }

        return new ItemViewBindingHolder(DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isFooterProgress(position)) {
            return;
        }

        Post post = getItem(position);
        ItemViewBindingHolder itemViewBindingHolder = (ItemViewBindingHolder) holder;
        ImageView avatarView = itemViewBindingHolder.avatar;

        // whether need to download avatars
        // depends on settings and Wi-Fi status
        if (mDownloadPreferencesManager.isAvatarsDownload()) {
            avatarView.setVisibility(View.VISIBLE);

            String url = mDownloadPreferencesManager.isHighResolutionAvatarsDownload()
                    ? Api.getAvatarMediumUrl(post.getUserId())
                    : Api.getAvatarSmallUrl(post.getUserId());
            // show user's avatar
            mAvatarRequestBuilder.signature(mDownloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature())
                    .load(url)
                    .into(avatarView);
        } else {
            avatarView.setVisibility(View.GONE);
        }

        itemViewBindingHolder.username.setText(post.getUsername());
        itemViewBindingHolder.datetime.setText(DateUtils.getRelativeDateTimeString(mContext,
                post.getDatetime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));

        TextView countView = itemViewBindingHolder.count;
        // there is no need to quote #1
        if ("1".equals(post.getCount())) {
            countView.setText("#1");
            countView.setClickable(false);
            countView.setLongClickable(false);
            countView.setFocusable(false);
            countView.setTag(R.id.post_tag, null);
        } else {
            Spannable spannable = new SpannableString("#" + post.getCount());
            spannable.setSpan(QUOTE_CLICKABLE_SPAN, 0, spannable.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            countView.setText(spannable);
            countView.setClickable(true);
            countView.setLongClickable(true);
            countView.setFocusable(true);
            countView.setTag(R.id.post_tag, post.getPartForQuote());
        }

        String reply = post.getReply();
        TextView replayView = itemViewBindingHolder.post;
        if (TextUtils.isEmpty(reply)) {
            replayView.setText(null);
        } else {
            // use GlideImageGetter to show images in TextView
            replayView.setText(Html.fromHtml(reply, new GlideImageGetter(mContext, replayView),
                    new TagHandler(mContext)));
        }
    }

    @Override
    public long getItemId(int position) {
        if (isFooterProgress(position)) {
            return Integer.MIN_VALUE;
        }

        return Long.parseLong(getItem(position).getCount());
    }

    private boolean isFooterProgress(int position) {
        return position == getItemCount() - 1 && getItem(position) == null;
    }

    public void showFooterProgress() {
        int position = getItemCount() - 1;
        // use null as ProgressBar's item
        Preconditions.checkState(getItem(position) != null);
        addItem(null);
        notifyItemInserted(position + 1);
    }

    public void hideFooterProgress() {
        int position = getItemCount() - 1;
        Preconditions.checkState(getItem(position) == null);
        removeItem(position);
        notifyItemRemoved(position);
    }

    private static class ItemViewBindingHolder extends RecyclerView.ViewHolder {

        private final ImageView avatar;
        private final TextView username;
        private final TextView datetime;
        private final TextView count;
        private final TextView post;

        public ItemViewBindingHolder(ItemPostBinding itemPostBinding) {
            super(itemPostBinding.getRoot());

            avatar = itemPostBinding.avatar;
            username = itemPostBinding.username;
            datetime = itemPostBinding.datetime;
            count = itemPostBinding.count;
            post = itemPostBinding.post;
        }
    }

    private static final ClickableSpan QUOTE_CLICKABLE_SPAN = new ClickableSpan() {

        @Override
        public void onClick(View widget) {
            Post post = (Post) widget.getTag(R.id.post_tag);
            App.getAppComponent(widget.getContext()).getEventBus().post(new QuoteEvent(post.getId(),
                    post.getCount()));
        }
    };

    private static class FooterProgressViewHolder extends RecyclerView.ViewHolder {

        public FooterProgressViewHolder(View itemView) {
            super(itemView);
        }
    }
}
