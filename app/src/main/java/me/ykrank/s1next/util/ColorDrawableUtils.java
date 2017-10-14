package me.ykrank.s1next.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DrawableUtils;

import java.util.Arrays;

/**
 * Created by ykrank on 2017/9/27.
 */

public class ColorDrawableUtils {

    /**
     * drawable 复制
     *
     * @param drawable
     * @return
     */
    @NonNull
    public static <T extends Drawable> T getNewDrawable(@NonNull T drawable) {
        //noinspection ConstantConditions
        return (T) drawable.getConstantState().newDrawable();
    }

    @NonNull
    public static <T extends Drawable> T safeMutableDrawable(@NonNull T drawable) {
        T tDrawable;
        if (DrawableUtils.canSafelyMutateDrawable(drawable)) {
            tDrawable = (T) drawable.mutate();
        } else {
            tDrawable = ColorDrawableUtils.getNewDrawable(drawable);
        }
        return tDrawable;
    }

    /**
     * 通过颜色构建ColorStateList。
     */
    public static ColorStateList createColorStateList(int normal, int pressed, int focused, int unable) {
        int[] colors = new int[]{pressed, focused, normal, focused, unable, normal};
        int[][] states = new int[6][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{android.R.attr.state_focused};
        states[4] = new int[]{android.R.attr.state_window_focused};
        states[5] = new int[]{};
        return new ColorStateList(states, colors);
    }

    /**
     * 通过颜色构建ColorStateList。
     */
    public static ColorStateList createColorStateList(int normal, int pressed, int unable) {
        int[] colors = new int[]{pressed, normal, unable, normal};
        int[][] states = new int[4][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled};
        states[2] = new int[]{android.R.attr.state_window_focused};
        states[3] = new int[]{};
        return new ColorStateList(states, colors);
    }

    /**
     * 通过颜色构建ColorStateList。
     */
    public static ColorStateList createColorStateList(int normal, int unable) {
        int[] colors = new int[]{normal, unable, normal};
        int[][] states = new int[3][];
        states[0] = new int[]{android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_window_focused};
        states[2] = new int[]{};
        return new ColorStateList(states, colors);
    }

    /**
     * 通过Drawable构建ColorStateList。
     */
    public static StateListDrawable createStateListDrawable(Drawable normal, Drawable pressed, Drawable focused, Drawable unable) {
        StateListDrawable bg = new StateListDrawable();
        // View.PRESSED_ENABLED_STATE_SET  
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        // View.ENABLED_FOCUSED_STATE_SET  
        bg.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focused);
        // View.ENABLED_STATE_SET  
        bg.addState(new int[]{android.R.attr.state_enabled}, normal);
        // View.FOCUSED_STATE_SET  
        bg.addState(new int[]{android.R.attr.state_focused}, focused);
        // View.WINDOW_FOCUSED_STATE_SET  
        bg.addState(new int[]{android.R.attr.state_window_focused}, unable);
        // View.EMPTY_STATE_SET  
        bg.addState(new int[]{}, normal);
        return bg;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    public static RippleDrawable getRippleDrawable(@Nullable Drawable content, int ripple) {
        return new RippleDrawable(ColorStateList.valueOf(ripple), content, getRippleMask(4));
    }

    private static Drawable getRippleMask(int radius) {
        float[] outerRadii = new float[8];
        // 3 is radius of final ripple, 
        // instead of 3 you can give required final radius
        Arrays.fill(outerRadii, radius);

        RoundRectShape r = new RoundRectShape(outerRadii, null, null);
        return new ShapeDrawable(r);
    }

    public static int getDarkerColor(int color) {
        float ratio = 1.0f - 0.2f;
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * ratio);
        int g = (int) (((color >> 8) & 0xFF) * ratio);
        int b = (int) ((color & 0xFF) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int getBrighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv); // convert to hsv

        hsv[1] = hsv[1] - 0.1f; // less saturation
        hsv[2] = hsv[2] + 0.1f; // more brightness
        return Color.HSVToColor(hsv);
    }
}
