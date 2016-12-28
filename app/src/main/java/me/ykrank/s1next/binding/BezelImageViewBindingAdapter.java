package me.ykrank.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.event.BlackListAddEvent;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.util.ActivityUtils;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.activity.GalleryActivity;
import me.ykrank.s1next.view.dialog.BlackListRemarkDialogFragment;
import me.ykrank.s1next.widget.BezelImageView;
import me.ykrank.s1next.widget.EventBus;
import me.ykrank.s1next.widget.glide.AvatarUrlsCache;
import me.ykrank.s1next.widget.glide.OriginalKey;

public final class BezelImageViewBindingAdapter {

    private BezelImageViewBindingAdapter() {
    }

    /**
     * Show default avatar if user hasn't logged in,
     * otherwise show user's avatar.
     */
    @BindingAdapter("user")
    public static void loadUserAvatar(BezelImageView bezelImageView, User user) {
        Context context = bezelImageView.getContext();

        //in device before 4.4, destroyed activity will cause glide error
        if (ActivityUtils.isActivityDestroyedForGlide(context)) {
            return;
        }

        DownloadPreferencesManager downloadPreferencesManager = App.getPrefComponent(context)
                .getDownloadPreferencesManager();
        if (user.isLogged()) {
            AvatarUrlsCache.clearUserAvatarCache(user.getUid());
            // setup user's avatar
            Glide.with(context)
                    .load(Api.getAvatarMediumUrl(user.getUid()))
                    .error(R.drawable.ic_drawer_avatar_placeholder)
                    .signature(downloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature())
                    .transform(new CenterCrop(Glide.get(context).getBitmapPool()))
                    .into(bezelImageView);
        } else {
            // setup default avatar
            Glide.with(context)
                    .load(R.drawable.ic_drawer_avatar_placeholder)
                    .signature(downloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature())
                    .transform(new CenterCrop(Glide.get(context).getBitmapPool()))
                    .into(bezelImageView);
        }
    }

    @BindingAdapter({"eventBus", "avatarDrawableRequestBuilder", "downloadPreferencesManager", "post"})
    public static void loadAuthorAvatar(BezelImageView bezelImageView, EventBus eventBus,
                                        DrawableRequestBuilder<String> avatarDrawableRequestBuilder,
                                        DownloadPreferencesManager downloadPreferencesManager,
                                        Post post) {
        // whether need to download avatars
        // depends on settings and Wi-Fi status
        if (downloadPreferencesManager.isAvatarsDownload()) {
            bezelImageView.setVisibility(View.VISIBLE);

            String url = downloadPreferencesManager.isHighResolutionAvatarsDownload()
                    ? Api.getAvatarMediumUrl(post.getAuthorId())
                    : Api.getAvatarSmallUrl(post.getAuthorId());
            // show user's avatar
            avatarDrawableRequestBuilder.signature(
                    downloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature())
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(bezelImageView);

            //点击显示头像大图
            //同时刷新错误头像的列表
            bezelImageView.setOnClickListener(v -> {
                String bigAvatarUrl = Api.getAvatarBigUrl(post.getAuthorId());
                AvatarUrlsCache.remove(OriginalKey.Builder.getInstance().obtainAvatarKey(downloadPreferencesManager, url));
                AvatarUrlsCache.remove(OriginalKey.Builder.getInstance().obtainAvatarKey(downloadPreferencesManager, bigAvatarUrl));
                GalleryActivity.startGalleryActivity(v.getContext(), bigAvatarUrl, url);
            });
            //长按显示抹布菜单
            bezelImageView.setOnLongClickListener((View v) -> {
                PopupMenu popup = new PopupMenu(bezelImageView.getContext(), v);
                popup.setOnMenuItemClickListener((MenuItem menuitem) -> {
                    switch (menuitem.getItemId()) {
                        case R.id.menu_popup_blacklist:
                            if (menuitem.getTitle().equals(bezelImageView.getContext().getString(R.string.menu_blacklist_remove))) {
                                eventBus.post(new BlackListAddEvent(Integer.valueOf(post.getAuthorId()),
                                        post.getAuthorName(), null, false));
                            } else {
                                Context context = ActivityUtils.getBaseContext(v.getContext());
                                if (context instanceof FragmentActivity) {
                                    BlackListRemarkDialogFragment.newInstance(Integer.valueOf(post.getAuthorId()),
                                            post.getAuthorName()).show(((FragmentActivity) context).getSupportFragmentManager(),
                                            BlackListRemarkDialogFragment.TAG);
                                } else {
                                    L.report(new IllegalStateException("抹布时头像Context不为FragmentActivity" + context));
                                }
                            }
                            return true;
                        default:
                            return false;
                    }
                });
                popup.inflate(R.menu.popup_blacklist);
                if (post.isHide()) {
                    popup.getMenu().findItem(R.id.menu_popup_blacklist).setTitle(R.string.menu_blacklist_remove);
                }
                popup.show();
                return true;
            });
        } else {
            bezelImageView.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("uid")
    public static void loadPmAvatar(BezelImageView bezelImageView, String uid) {
        Context context = bezelImageView.getContext();
        DownloadPreferencesManager downloadPreferencesManager = App.getPrefComponent(context)
                .getDownloadPreferencesManager();
        Glide.with(context)
                .load(Api.getAvatarMediumUrl(uid))
                .error(R.drawable.ic_drawer_avatar_placeholder)
                .signature(downloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature())
                .transform(new CenterCrop(Glide.get(context).getBitmapPool()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(bezelImageView);

    }
}
