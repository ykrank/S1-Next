package cl.monsoon.s1next.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;

import java.util.Map;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.Config;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Post;
import cl.monsoon.s1next.widget.GlideImageGetter;
import cl.monsoon.s1next.widget.RoundedBitmapTransformation;

public final class PostListRecyclerAdapter extends RecyclerAdapter<Post, PostListRecyclerAdapter.ViewHolder> {

    private final Context mContext;

    private final DrawableRequestBuilder<String> mAvatarRequestBuilder;
    private final DrawableRequestBuilder<String> mImageGetterRequestBuilder;

    public PostListRecyclerAdapter(Context context) {
        this.mContext = context;

        setHasStableIds(true);

        // loading avatars is prior to images in reply
        mAvatarRequestBuilder =
                Glide.with(mContext)
                        .from(String.class)
                        .error(R.drawable.ic_avatar_placeholder)
                        .priority(Priority.HIGH)
                        .transform(new RoundedBitmapTransformation(mContext));

        // used in GlideImageGetter
        mImageGetterRequestBuilder =
                Glide.with(mContext)
                        .from(String.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(
                        viewGroup.getContext())
                        .inflate(R.layout.fragment_post_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Post post = mList.get(i);

        ImageView avatarView = viewHolder.mAvatar;

        // whether need download avatars depends settings and Wi-Fi status
        boolean avatarsDownload = Config.isAvatarsDownload();
        if (avatarsDownload) {
            avatarView.setVisibility(View.VISIBLE);

            // show user's avatar
            mAvatarRequestBuilder
                    .load(Api.getUrlAvatarSmall(post.getUserId()))
                    .into(avatarView);
        } else {
            avatarView.setVisibility(View.GONE);
        }

        viewHolder.mUsername.setText(post.getUsername());
        viewHolder.mTime.setText(
                DateUtils.getRelativeDateTimeString(
                        mContext,
                        post.getTime(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.DAY_IN_MILLIS,
                        0));
        viewHolder.mCount.setText("#" + post.getCount());

        String url;
        String reply = post.getReply();
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
        TextView replayView = viewHolder.mReply;
        replayView.setText(
                Html.fromHtml(
                        reply,
                        new GlideImageGetter(replayView, mImageGetterRequestBuilder),
                        null));
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mList.get(position).getCount());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mAvatar;
        private final TextView mUsername;
        private final TextView mTime;
        private final TextView mCount;
        private final TextView mReply;

        public ViewHolder(View itemView) {
            super(itemView);

            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mUsername = (TextView) itemView.findViewById(R.id.drawer_username);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mCount = (TextView) itemView.findViewById(R.id.count);
            mReply = (TextView) itemView.findViewById(R.id.reply);

            // make link clickable
            mReply.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
