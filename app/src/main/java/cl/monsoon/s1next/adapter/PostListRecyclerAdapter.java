package cl.monsoon.s1next.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
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

import java.util.Map;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.activity.PostListActivity;
import cl.monsoon.s1next.activity.ReplyActivity;
import cl.monsoon.s1next.model.Post;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.widget.GlideImageGetter;
import cl.monsoon.s1next.widget.ImageTagHandler;
import cl.monsoon.s1next.widget.MyMovementMethod;

public final class PostListRecyclerAdapter
        extends RecyclerAdapter<Post, RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER_PROGRESS = 1;

    private boolean mHasFooterProgress;

    private final Context mContext;

    private final DrawableRequestBuilder<String> mAvatarRequestBuilder;
    private final DrawableRequestBuilder<String> mImageGetterRequestBuilder;

    public PostListRecyclerAdapter(Context context) {
        this.mContext = context;

        setHasStableIds(true);

        // Lading avatars is prior to images in replies
        mAvatarRequestBuilder =
                Glide.with(mContext)
                        .from(String.class)
                        .error(R.drawable.ic_avatar_placeholder)
                        .priority(Priority.HIGH)
                        .transform(new CenterCrop(Glide.get(context).getBitmapPool()));

        // used in GlideImageGetter
        mImageGetterRequestBuilder =
                Glide.with(mContext)
                        .from(String.class);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER_PROGRESS) {
            View view =
                    LayoutInflater.from(
                            parent.getContext())
                            .inflate(R.layout.fragment_post_list_footer_progress, parent, false);

            return new FooterProgressViewHolder(view);
        }

        View view =
                LayoutInflater.from(
                        parent.getContext())
                        .inflate(R.layout.fragment_post_list, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterProgress(position)) {
            return TYPE_FOOTER_PROGRESS;
        }

        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isFooterProgress(position)) {
            return;
        }

        ItemViewHolder itemViewHolder;
        if (holder instanceof ItemViewHolder) {
            itemViewHolder = (ItemViewHolder) holder;
        } else {
            throw new ClassCastException(holder + "must extend ItemViewHolder.");
        }

        Post post = mList.get(position);

        ImageView avatarView = itemViewHolder.mAvatar;

        // whether need download avatars depends on settings and Wi-Fi status
        final boolean avatarsDownload = Config.isAvatarsDownload();
        if (avatarsDownload) {
            avatarView.setVisibility(View.VISIBLE);

            // show user's avatar
            mAvatarRequestBuilder
                    .load(Api.getUrlAvatarSmall(post.getUserId()))
                    .into(avatarView);
        } else {
            avatarView.setVisibility(View.GONE);
        }

        itemViewHolder.mUsername.setText(post.getUsername());
        itemViewHolder.mTime.setText(
                DateUtils.getRelativeDateTimeString(
                        mContext,
                        post.getTime(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.DAY_IN_MILLIS,
                        0));

        TextView countView = itemViewHolder.mCount;
        // there is no need to quote #1
        if (post.getCount().equals("1")) {
            countView.setText("#1");
        } else {
            Spannable spannable = new SpannableString("#" + post.getCount());
            spannable.setSpan(
                    MyClickableSpan, 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            countView.setText(spannable);
            countView.setTag(post.getCount());
        }

        String reply = post.getReply();
        TextView replayView = itemViewHolder.mPost;
        // some replies are empty
        // like http://bbs.saraba1st.com/2b/thread-1008413-1-1.html#authorposton24836448
        if (TextUtils.isEmpty(reply)) {
            replayView.setText(null);

            return;
        }

        String url;
        Post.AttachmentWrapper attachmentWrapper = post.getAttachmentWrapper();
        // whether this post has attachment images
        if (attachmentWrapper != null) {
            Map<String, Post.Attachment> attachmentMap =
                    post.getAttachmentWrapper().getAttachmentMap();

            for (Map.Entry<String, Post.Attachment> entry : attachmentMap.entrySet()) {
                Post.Attachment attachment = entry.getValue();
                url = attachment.getUrl();

                // Replace attach tag with HTML img tag
                // in order to display attachment images in TextView.
                reply = reply.replace(
                        "[attach]" + entry.getKey() + "[/attach]", "<img src=\"" + url + "\" />");
            }
        }

        // use GlideImageGetter to show images
        replayView.setText(
                Html.fromHtml(
                        reply,
                        new GlideImageGetter(replayView, mImageGetterRequestBuilder),
                        new ImageTagHandler(mContext)));
    }

    @Override
    public long getItemId(int position) {
        if (isFooterProgress(position)) {
            return Integer.MIN_VALUE;
        }

        return Long.parseLong(mList.get(position).getCount());
    }

    private boolean isFooterProgress(int position) {
        return mHasFooterProgress && position == getItemCount() - 1;
    }

    public void showFooterProgress() {
        if (mHasFooterProgress) {
            throw new IllegalStateException(this + " already has footer progress.");
        }

        mHasFooterProgress = true;
        mList.add(null);
        notifyItemChanged(getItemCount() - 1);
    }

    public void hideFooterProgress() {
        int position = getItemCount() - 1;
        mList.remove(position);
        mHasFooterProgress = false;
        notifyItemRemoved(position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mAvatar;
        private final TextView mUsername;
        private final TextView mTime;
        private final TextView mCount;
        private final TextView mPost;

        public ItemViewHolder(View itemView) {
            super(itemView);

            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mUsername = (TextView) itemView.findViewById(R.id.username);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mCount = (TextView) itemView.findViewById(R.id.count);
            mPost = (TextView) itemView.findViewById(R.id.post);

            Config.updateTextSize(mUsername);
            Config.updateTextSize(mTime);
            Config.updateTextSize(mCount);
            Config.updateTextSize(mPost);

            mCount.setMovementMethod(LinkMovementMethod.getInstance());
            // use custom movement method to provides selection and click
            mPost.setMovementMethod(MyMovementMethod.getInstance());
        }
    }

    private static final ClickableSpan MyClickableSpan = new ClickableSpan() {

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(PostListActivity.ACTION_QUOTE);
            intent.putExtra(ReplyActivity.ARG_QUOTE_COUNT, (CharSequence) widget.getTag());

            widget.getContext().sendBroadcast(intent);
        }
    };

    public static class FooterProgressViewHolder extends RecyclerView.ViewHolder {

        public FooterProgressViewHolder(View itemView) {
            super(itemView);
        }
    }
}
