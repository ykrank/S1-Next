package cl.monsoon.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.view.View;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.Api;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.widget.BezelImageView;

public final class BezelImageViewBindingAdapter {

    private BezelImageViewBindingAdapter() {}

    /**
     * Show default avatar if user hasn't logged in,
     * otherwise show user's avatar.
     */
    @BindingAdapter("user")
    public static void loadAvatar(BezelImageView bezelImageView, User user) {
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

    @BindingAdapter({"avatarDrawableRequestBuilder", "downloadPreferencesManager", "authorId"})
    public static void loadAvatar(BezelImageView bezelImageView,
                                  DrawableRequestBuilder<String> avatarDrawableRequestBuilder,
                                  DownloadPreferencesManager downloadPreferencesManager,
                                  String authorId) {
        // whether need to download avatars
        // depends on settings and Wi-Fi status
        if (downloadPreferencesManager.isAvatarsDownload()) {
            bezelImageView.setVisibility(View.VISIBLE);

            String url = downloadPreferencesManager.isHighResolutionAvatarsDownload()
                    ? Api.getAvatarMediumUrl(authorId)
                    : Api.getAvatarSmallUrl(authorId);
            // show user's avatar
            avatarDrawableRequestBuilder.signature(
                    downloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature())
                    .load(url)
                    .into(bezelImageView);
        } else {
            bezelImageView.setVisibility(View.GONE);
        }
    }
}
