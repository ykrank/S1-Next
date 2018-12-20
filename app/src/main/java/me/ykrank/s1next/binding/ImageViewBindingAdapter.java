package me.ykrank.s1next.binding;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.ykrank.androidtools.util.ContextUtils;
import com.github.ykrank.androidtools.util.L;
import com.github.ykrank.androidtools.util.RxJavaUtil;
import com.github.ykrank.androidtools.widget.glide.model.ImageInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.widget.glide.AvatarUrlsCache;
import me.ykrank.s1next.widget.glide.model.AvatarUrl;

public final class ImageViewBindingAdapter {

    private ImageViewBindingAdapter() {
    }

    @BindingAdapter({"emoticonRequestManager", "emoticonImagePath"})
    public static void loadEmoticon(ImageView imageView,
                                    RequestManager requestManager,
                                    String emoticonImagePath) {
        requestManager.load(Uri.parse(emoticonImagePath)).into(imageView);
    }

    /**
     * Show default avatar if user hasn't logged in,
     * otherwise show user's avatar.
     */
    @BindingAdapter("user")
    public static void loadUserAvatar(ImageView bezelImageView, User user) {
        //in device before 4.4, destroyed activity will cause glide error
        if (ContextUtils.isActivityDestroyedForGlide(bezelImageView.getContext())) {
            return;
        }
        DownloadPreferencesManager downloadPreferencesManager = App.Companion.getPreAppComponent()
                .getDownloadPreferencesManager();
        if (user.isLogged()) {
            RequestManager requestManager = Glide.with(bezelImageView);
            bezelImageView.setTag(R.id.tag_drawable_info, null);
            AvatarUrlsCache.clearUserAvatarCache(user.getUid());
            // setup user's avatar
            requestManager
                    .load(new AvatarUrl(Api.getAvatarMediumUrl(user.getUid())))
                    .apply(new RequestOptions()
                            .circleCrop()
                            .signature(downloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature())
                    )
                    .error(requestManager
                            .load(R.drawable.ic_drawer_avatar_placeholder)
                            .apply(RequestOptions.circleCropTransform()))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //You can't start or clear loads in RequestListener or Target callbacks.
                            bezelImageView.setTag(R.id.tag_drawable_info, null);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            bezelImageView.setTag(R.id.tag_drawable_info, new ImageInfo(((AvatarUrl) model).toStringUrl(), resource.getIntrinsicWidth(), resource.getIntrinsicHeight()));
                            return false;
                        }
                    })
                    .into(bezelImageView);
        } else {
            // setup default avatar
            loadPlaceHolderAvatar(bezelImageView);
        }
    }

    @BindingAdapter("uid")
    public static void loadAvatar(ImageView bezelImageView, int oldUid, int newUid) {
        loadAvatar(bezelImageView, String.valueOf(oldUid), String.valueOf(newUid));
    }

    @BindingAdapter("uid")
    public static void loadAvatar(ImageView bezelImageView, String oldUid, String newUid) {
        if (TextUtils.equals(oldUid, newUid)) {
            return;
        }
        DownloadPreferencesManager downloadPreferencesManager = App.Companion.getPreAppComponent()
                .getDownloadPreferencesManager();
        loadAvatar(bezelImageView, null, null, false, false, null, downloadPreferencesManager, newUid, false, false, null);
    }

    @BindingAdapter({"downloadPreferencesManager", "uid", "big", "preLoad", "thumb"})
    public static void loadAvatar(ImageView bezelImageView,
                                  DownloadPreferencesManager oldManager,
                                  String oldUid, boolean oldBig, boolean oldPreLoad, String oldThumbUrl,
                                  DownloadPreferencesManager newManager,
                                  String newUid, boolean newBig, boolean newPreLoad, String newThumbUrl) {
        if (oldManager == newManager && TextUtils.equals(oldUid, newUid) && oldBig == newBig &&
                oldPreLoad == newPreLoad && TextUtils.equals(oldThumbUrl, newThumbUrl)) {
            return;
        }
        //in device before 4.4, destroyed activity will cause glide error
        if (ContextUtils.isActivityDestroyedForGlide(bezelImageView.getContext())) {
            return;
        }
        if (TextUtils.isEmpty(newUid)) {
            loadPlaceHolderAvatar(bezelImageView);
        } else {
            loadRoundAvatar(bezelImageView, newManager, newUid, newBig, newPreLoad, newThumbUrl);
        }
    }

    private static void loadPlaceHolderAvatar(ImageView imageView) {
        imageView.setTag(R.id.tag_drawable_info, null);
        Glide.with(imageView)
                .load(R.drawable.ic_drawer_avatar_placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    private static void loadRoundAvatar(ImageView imageView, DownloadPreferencesManager downloadPreferencesManager,
                                        String uid, boolean isBig, boolean preLoad, String thumbUrl) {
        String smallAvatarUrl = Api.getAvatarSmallUrl(uid);
        String mediumAvatarUrl = Api.getAvatarMediumUrl(uid);
        List<String> urls = new LinkedList<>();
        if (isBig) {
            //Load big avatar, then load medium and small avatar if failed
            String bigAvatarUrl = Api.getAvatarBigUrl(uid);
            urls.add(bigAvatarUrl);
            urls.add(mediumAvatarUrl);
        } else if (downloadPreferencesManager.isHighResolutionAvatarsDownload()) {
            //if load high resolution, then load medium avatar a high priority
            urls.add(mediumAvatarUrl);
        }
        urls.add(smallAvatarUrl);
        if (preLoad) {
            if (isBig) { //show thumb
                List<String> thumbUrls = new ArrayList<>();
                thumbUrls.add(thumbUrl);
                loadRoundAvatar(imageView, downloadPreferencesManager, thumbUrls, null, true);
            }
            preloadRoundAvatar(imageView, downloadPreferencesManager, urls);
        } else {
            loadRoundAvatar(imageView, downloadPreferencesManager, urls, thumbUrl, isBig);
        }
    }

    private static void preloadRoundAvatar(ImageView imageView, DownloadPreferencesManager downloadPreferencesManager,
                                           List<String> urls) {
        if (urls == null || urls.isEmpty() || TextUtils.isEmpty(urls.get(0))) {
            return;
        }
        RequestBuilder<Drawable> listener = Glide.with(imageView)
                .load(new AvatarUrl(urls.get(0)))
                .apply(new RequestOptions()
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ic_drawer_avatar_placeholder)
                        .signature(downloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature())
                        .priority(Priority.LOW)
                )
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (urls.size() > 0) {
                            urls.remove(0);
                            RxJavaUtil.workInMainThreadWithView(imageView, () -> preloadRoundAvatar(imageView, downloadPreferencesManager, urls));
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                });
        listener.preload();
    }

    private static void loadRoundAvatar(ImageView imageView, DownloadPreferencesManager downloadPreferencesManager,
                                        List<String> urls, String thumbUrl, boolean fade) {
        if (urls == null || urls.isEmpty() || TextUtils.isEmpty(urls.get(0))) {
            loadPlaceHolderAvatar(imageView);
            return;
        }
        RequestManager requestManager = Glide.with(imageView);

        RequestBuilder<Drawable> listener = requestManager
                .load(new AvatarUrl(urls.get(0)))
                .apply(new RequestOptions()
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(downloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature())
                        .priority(Priority.HIGH)
                )
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        urls.remove(0);
                        RxJavaUtil.workInMainThreadWithView(imageView, () -> loadRoundAvatar(imageView, downloadPreferencesManager, urls, thumbUrl, fade));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        AvatarUrl avatarUrl = (AvatarUrl) model;
                        L.l("Load avatar:" + avatarUrl.toStringUrl());
                        imageView.setTag(R.id.tag_drawable_info, new ImageInfo(avatarUrl.toStringUrl(), resource.getIntrinsicWidth(), resource.getIntrinsicHeight()));
                        return false;
                    }
                });
        if (!TextUtils.isEmpty(thumbUrl)) {
            listener = listener.thumbnail(
                    requestManager
                            .load(new AvatarUrl(thumbUrl))
                            .apply(new RequestOptions()
                                    .circleCrop()
                                    .signature(downloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature())
                                    .diskCacheStrategy(DiskCacheStrategy.DATA)));
        } else {
            listener = listener.thumbnail(requestManager.load(R.drawable.ic_drawer_avatar_placeholder).apply(RequestOptions.circleCropTransform()));
        }
        if (fade) {
            listener = listener.transition(DrawableTransitionOptions.withCrossFade(300));
        }
        listener.into(imageView);

    }
}
