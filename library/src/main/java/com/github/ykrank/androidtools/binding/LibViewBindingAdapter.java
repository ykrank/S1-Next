package com.github.ykrank.androidtools.binding;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import androidx.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.TintableBackgroundView;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.cardview.widget.CardView;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Toast;

import com.github.ykrank.androidtools.R;
import com.github.ykrank.androidtools.extension.ContextExtensionKt;
import com.github.ykrank.androidtools.util.ColorDrawableUtils;
import com.github.ykrank.androidtools.util.ResourceUtil;
import com.google.common.base.Objects;


public final class LibViewBindingAdapter {

    private LibViewBindingAdapter() {
    }

    @BindingAdapter("clickToast")
    public static void clickToast(View view, String msg) {
        view.setOnClickListener(v -> {
            ContextExtensionKt.toast(v.getContext(), msg, Toast.LENGTH_SHORT);
        });
    }

    private static final int UNABLE = Color.LTGRAY;
    //Default use darker color
    private static final int RIPPLE_DEFAULT = Integer.MIN_VALUE;
    private static final ColorDrawable UNABLE_DRAWABLE = new ColorDrawable(UNABLE);

    @BindingAdapter("cardTintColor")
    public static void setCardBackgroundTint(CardView view, @ColorInt Integer oldTintColor, @ColorInt Integer tintColor) {
        if (Objects.equal(oldTintColor, tintColor)){
            return;
        }
        if (tintColor == null || tintColor == Integer.MIN_VALUE) {
            TypedArray ta = view.getContext().obtainStyledAttributes(ResourceUtil.getResourceId(view.getContext(), R.attr.materialCardViewStyle), new int[]{R.attr.cardBackgroundColor});
            tintColor = ta.getColor(0, Color.TRANSPARENT);
        }
        view.setCardBackgroundColor(tintColor);
    }

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
    public static void setRipple(View view, @ColorInt int oRipple, @ColorInt int ripple) {
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

    @BindingAdapter("increaseClickingArea")
    public static void increaseClickingArea(View view, float size) {
        // fork from http://stackoverflow.com/a/1343796
        View parent = (View) view.getParent();
        // post in the parent's message queue to make sure the parent
        // lays out its children before we call View#getHitRect()
        parent.post(() -> {
            final int halfSize = (int) (size / 2 + 0.5);
            Rect rect = new Rect();
            view.getHitRect(rect);
            rect.top -= halfSize;
            rect.right += halfSize;
            rect.bottom += halfSize;
            rect.left -= halfSize;
            // use TouchDelegate to increase count's clicking area
            parent.setTouchDelegate(new TouchDelegate(rect, view));
        });
    }

    @BindingAdapter({"normalBackground", "backProgress"})
    public static void showViewProgressBack(View view, Drawable oNormalDrawable, boolean oBackProgress,
                                            Drawable normalDrawable, boolean backProgress) {
        if (oBackProgress && backProgress) {
            return;
        }
        if (backProgress) {
            CircularProgressDrawable drawable = new CircularProgressDrawable(view.getContext());
            ViewCompat.setBackground(view, drawable);
        } else {
            ViewCompat.setBackground(view, normalDrawable);
        }
    }
}
