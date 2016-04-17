package cl.monsoon.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.Api;
import cl.monsoon.s1next.data.api.model.Post;
import cl.monsoon.s1next.data.event.BlackListAddEvent;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.view.activity.GalleryActivity;
import cl.monsoon.s1next.widget.BezelImageView;
import cl.monsoon.s1next.widget.EventBus;

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
        DownloadPreferencesManager downloadPreferencesManager = App.getAppComponent(context)
                .getDownloadPreferencesManager();
        if (user.isLogged()) {
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
                    .into(bezelImageView);

            //点击显示头像大图
            bezelImageView.setOnClickListener(v -> {
                GalleryActivity.startGalleryActivity(v.getContext(), Api.getAvatarBigUrl(post.getAuthorId()));
            });
            //长按显示抹布菜单
            bezelImageView.setOnLongClickListener((View v) -> {
                PopupMenu popup = new PopupMenu(bezelImageView.getContext(), v);
                popup.setOnMenuItemClickListener((MenuItem menuitem) -> {
                    switch (menuitem.getItemId()) {
                        case R.id.menu_post_blacklist:
                            if (menuitem.getTitle().equals(bezelImageView.getContext().getString(R.string.menu_blacklist_remove))) {
                                eventBus.post(new BlackListAddEvent(Integer.valueOf(post.getAuthorId()), 
                                        post.getAuthorName(), false));
                            } else {
                                eventBus.post(new BlackListAddEvent(Integer.valueOf(post.getAuthorId()),
                                        post.getAuthorName(), true));
                            }
                            return true;
                        default:
                            return false;
                    }
                });
                popup.inflate(R.menu.post_blacklist);
                if (post.isHide()) {
                    popup.getMenu().findItem(R.id.menu_post_blacklist).setTitle(R.string.menu_blacklist_remove);
                }
                popup.show();
                return true;
            });
        } else {
            bezelImageView.setVisibility(View.GONE);
        }
    }
}
