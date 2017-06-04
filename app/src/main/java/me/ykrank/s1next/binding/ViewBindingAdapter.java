package me.ykrank.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import io.reactivex.functions.Consumer;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.widget.glide.transformations.BlurTransformation;
import me.ykrank.s1next.widget.glide.viewtarget.DrawableViewBackgroundTarget;
import me.ykrank.s1next.widget.glide.viewtarget.ViewBackgroundTarget;

/**
 * Created by AdminYkrank on 2016/4/17.
 */
public final class ViewBindingAdapter {
    private ViewBindingAdapter() {
    }

    @BindingAdapter("backTintColor")
    public static void setBackgroundTint(View view, @ColorInt int tintColor) {
        setBackgroundTint(view, tintColor, null);
    }

    @BindingAdapter({"backTintColor", "tintMode"})
    public static void setBackgroundTint(View view, @ColorInt int tintColor, @Nullable PorterDuff.Mode tintMode) {
        Drawable originalDrawable = view.getBackground();
        if (originalDrawable == null) {
            return;
        }
        Drawable tintDrawable = DrawableCompat.wrap(originalDrawable.mutate());
        if (tintMode != null) {
            DrawableCompat.setTintMode(tintDrawable, tintMode);
        }
        DrawableCompat.setTint(tintDrawable, tintColor);
        ViewCompat.setBackground(view, tintDrawable);
    }

    @BindingAdapter("marginEnd")
    public static void setMarginEnd(View view, float margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginEnd((int) margin);
        } else {
            layoutParams.rightMargin = (int) margin;
        }
        view.setLayoutParams(layoutParams);
    }

    /**
     * action when view init
     *
     * @param view       view
     * @param onViewBind action when view init
     */
    @BindingAdapter("bindEvent")
    public static void setOnViewBind(View view, Consumer<View> onViewBind) {
        try {
            onViewBind.accept(view);
        } catch (Exception e) {
            L.report(e);
        }
    }

    @BindingAdapter({"downloadPreferencesManager", "blurUid"})
    public static void setUserBlurBackground(View view, DownloadPreferencesManager oldManager, String oldBlurUid,
                                             DownloadPreferencesManager newManager, String newBlurUid) {
        if (TextUtils.isEmpty(newBlurUid)) {
            setBlurBackground(view, oldManager, null, newManager, null);
            return;
        }
        if (!TextUtils.equals(oldBlurUid, newBlurUid)) {
            String oldAvatarUrl, newAvatarUrl;
            if (newManager.isHighResolutionAvatarsDownload()) {
                oldAvatarUrl = Api.getAvatarMediumUrl(oldBlurUid);
                newAvatarUrl = Api.getAvatarMediumUrl(newBlurUid);
            } else {
                oldAvatarUrl = Api.getAvatarSmallUrl(oldBlurUid);
                newAvatarUrl = Api.getAvatarSmallUrl(newBlurUid);
            }
            setBlurBackground(view, oldManager, oldAvatarUrl, newManager, newAvatarUrl);
        }
    }

    @BindingAdapter({"downloadPreferencesManager", "blurUrl"})
    public static void setBlurBackground(View view, DownloadPreferencesManager oldManager, String oldBlurUrl,
                                         DownloadPreferencesManager newManager, String newBlurUrl) {
        // TODO: 2017/6/5 blur size error
        Context context = view.getContext();
        if (TextUtils.isEmpty(newBlurUrl)) {
            Glide.with(context)
                    .load(R.drawable.ic_avatar_placeholder)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .transform(new BlurTransformation(context, 25))
                    )
                    .transition(DrawableTransitionOptions.withCrossFade(android.R.anim.fade_in, 300))
                    .into(new DrawableViewBackgroundTarget(view));
            return;
        }
        if (!TextUtils.equals(oldBlurUrl, newBlurUrl)) {
            int radius = 10;
            if (newManager.isHighResolutionAvatarsDownload()) {
                radius = 20;
            }
            Glide.with(context)
                    .asBitmap()
                    .load(newBlurUrl)
                    .apply(new RequestOptions()
                            .signature(newManager.getAvatarCacheInvalidationIntervalSignature())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .centerCrop()
                            .transform(new BlurTransformation(context, radius))
                    )
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            setBlurBackground(view, oldManager, null, newManager, null);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(new ViewBackgroundTarget<Bitmap>(view) {

                        @Override
                        protected void setResource(Bitmap resource) {
                            setDrawable(new BitmapDrawable(getView().getResources(), resource));
                        }
                    });
        }
    }
}
