package me.ykrank.s1next.util;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.util.Arrays;

/**
 * Created by ykrank on 2017/9/27.
 */

public class DrawableUtils {

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
    public static Drawable getRippleDrawable(@Nullable Drawable content, int ripple, int radius) {
        return new RippleDrawable(ColorStateList.valueOf(ripple), content, getRippleMask(ripple, radius));
    }

    private static Drawable getRippleMask(int color, int radius) {
        float[] outerRadii = new float[8];
        // 3 is radius of final ripple, 
        // instead of 3 you can give required final radius
        Arrays.fill(outerRadii, radius);

        RoundRectShape r = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(r);
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }
}
