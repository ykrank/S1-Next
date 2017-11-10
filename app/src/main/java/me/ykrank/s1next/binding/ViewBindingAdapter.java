package me.ykrank.s1next.binding;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.TintableBackgroundView;
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
import com.github.ykrank.androidtools.util.ColorDrawableUtils;
import com.github.ykrank.androidtools.util.L;
import com.github.ykrank.androidtools.util.RxJavaUtil;
import com.github.ykrank.androidtools.widget.glide.transformations.BlurTransformation;
import com.github.ykrank.androidtools.widget.glide.viewtarget.DrawableViewBackgroundTarget;
import com.github.ykrank.androidtools.widget.glide.viewtarget.ViewBackgroundTarget;

import io.reactivex.functions.Consumer;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;

/**
 * Created by AdminYkrank on 2016/4/17.
 */
public final class ViewBindingAdapter {
    private ViewBindingAdapter() {
    }

    private static final int UNABLE = Color.LTGRAY;
    //Default use darker color
    private static final int RIPPLE_DEFAULT = Integer.MIN_VALUE;
    private static final ColorDrawable UNABLE_DRAWABLE = new ColorDrawable(UNABLE);

    @BindingAdapter("backTintColor")
    public static void setBackgroundTint(View view, @ColorInt int oldTintColor, @ColorInt int tintColor) {
        setBackgroundTint(view, oldTintColor, PorterDuff.Mode.SRC_IN, RIPPLE_DEFAULT, tintColor, PorterDuff.Mode.SRC_IN, RIPPLE_DEFAULT);
    }

    @BindingAdapter({"backTintColor", "tintMode"})
    public static void setBackgroundTint(View view, @ColorInt int oldTintColor, @Nullable PorterDuff.Mode oldTintMode,
                                         @ColorInt int tintColor, @Nullable PorterDuff.Mode tintMode) {
        setBackgroundTint(view, oldTintColor, oldTintMode, RIPPLE_DEFAULT, tintColor, tintMode, RIPPLE_DEFAULT);
    }

    @BindingAdapter({"backTintColor", "ripple"})
    public static void setBackgroundTint(View view, @ColorInt int oldTintColor, int oldRipple,
                                         @ColorInt int tintColor, int ripple) {
        setBackgroundTint(view, oldTintColor, PorterDuff.Mode.SRC_IN, oldRipple, tintColor, PorterDuff.Mode.SRC_IN, ripple);
    }

    @BindingAdapter({"backTintColor", "tintMode", "ripple"})
    public static void setBackgroundTint(View view, @ColorInt int oldTintColor, @Nullable PorterDuff.Mode oldTintMode, int oldRipple,
                                         @ColorInt int tintColor, @Nullable PorterDuff.Mode tintMode, int ripple) {
        if (oldTintColor == tintColor && oldTintMode == tintMode && oldRipple == ripple) {
            return;
        }
        Drawable originalDrawable = view.getBackground();
        if (originalDrawable == null) {
            return;
        }

        if (ripple == RIPPLE_DEFAULT) {
            ripple = ColorDrawableUtils.getDarkerColor(tintColor);
        }
        ColorStateList colorStateList = ColorDrawableUtils.createColorStateList(tintColor, UNABLE);

        //Appcompat view
        if (view instanceof TintableBackgroundView) {
            if (tintMode != null) {
                ((TintableBackgroundView) view).setSupportBackgroundTintMode(tintMode);
            }
            ((TintableBackgroundView) view).setSupportBackgroundTintList(colorStateList);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RippleDrawable tintDrawable;
                originalDrawable = view.getBackground();
                if (originalDrawable instanceof RippleDrawable) {
                    tintDrawable = ColorDrawableUtils.safeMutableDrawable((RippleDrawable) originalDrawable);
                    tintDrawable.setColor(ColorStateList.valueOf(ripple));
                } else {
                    tintDrawable = ColorDrawableUtils.getRippleDrawable(originalDrawable, ripple);
                }

                ViewCompat.setBackground(view, tintDrawable);
            }
            return;
        }

        Drawable tintDrawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tintDrawable = ColorDrawableUtils.safeMutableDrawable(originalDrawable);
            if (tintMode != null) {
                tintDrawable.setTintMode(tintMode);
            }
            tintDrawable.setTintList(colorStateList);
            if (tintDrawable instanceof RippleDrawable) {
                ((RippleDrawable) tintDrawable).setColor(ColorStateList.valueOf(ripple));
            } else {
                tintDrawable = ColorDrawableUtils.getRippleDrawable(tintDrawable, ripple);
            }
            ViewCompat.setBackground(view, tintDrawable);
        } else {
            tintDrawable = DrawableCompat.wrap(originalDrawable.mutate());
            if (tintMode != null) {
                DrawableCompat.setTintMode(tintDrawable, tintMode);
            }
            DrawableCompat.setTintList(tintDrawable, colorStateList);
            ViewCompat.setBackground(view, tintDrawable);
            if (tintDrawable == originalDrawable) {
                view.invalidate();
            }
        }
    }

    @BindingAdapter({"ripple"})
    public static void setRipple(View view, int oRipple, int ripple) {
        if (oRipple == ripple) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        Drawable tintDrawable = null;
        Drawable originalDrawable = view.getBackground();

        if (originalDrawable != null) {
            tintDrawable = ColorDrawableUtils.safeMutableDrawable(originalDrawable);
        }
        if (tintDrawable instanceof RippleDrawable) {
            ((RippleDrawable) tintDrawable).setColor(ColorStateList.valueOf(ripple));
        } else {
            tintDrawable = ColorDrawableUtils.getRippleDrawable(tintDrawable, ripple);
        }
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
        Context context = view.getContext();
        BlurTransformation blurTransformation = new BlurTransformation(context, 20);
        blurTransformation.setTargetSize(50);
        if (TextUtils.isEmpty(newBlurUrl)) {
            Glide.with(context)
                    .load(R.drawable.ic_avatar_placeholder)
                    .apply(new RequestOptions()
                            .transform(blurTransformation)
                    )
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(new DrawableViewBackgroundTarget(view));
            return;
        }
        if (!TextUtils.equals(oldBlurUrl, newBlurUrl)) {
            Glide.with(context)
                    .asBitmap()
                    .load(newBlurUrl)
                    .apply(new RequestOptions()
                            .signature(newManager.getAvatarCacheInvalidationIntervalSignature())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .transform(blurTransformation)
                    )
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            RxJavaUtil.workInMainThreadWithView(view, () -> setBlurBackground(view, oldManager, null, newManager, null));
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
