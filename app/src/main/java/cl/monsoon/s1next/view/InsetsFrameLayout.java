package cl.monsoon.s1next.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @see #setOnInsetsCallback(OnInsetsCallback)
 * <p>
 * Forked from https://github.com/google/iosched/blob/master/android/src/main/java/com/google/samples/apps/iosched/ui/widget/ScrimInsetsFrameLayout.java
 */
public final class InsetsFrameLayout extends FrameLayout {

    private OnInsetsCallback mOnInsetsCallback;

    public InsetsFrameLayout(Context context) {
        super(context);
    }

    public InsetsFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InsetsFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("UnusedDeclaration")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InsetsFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected boolean fitSystemWindows(Rect insets) {
        if (mOnInsetsCallback != null) {
            mOnInsetsCallback.onInsetsChanged(insets);
        }

        return true;
    }

    /**
     * Allows the calling container to specify a callback for custom processing
     * when insets change (i.e. when {@link #fitSystemWindows(Rect)} is called).
     * This is useful for setting padding on UI elements based on UI insets
     * (e.g. the Toolbar or a RecyclerView). When using with ListView or RecyclerView,
     * remember to set clipToPadding to false.
     */
    public void setOnInsetsCallback(OnInsetsCallback onInsetsCallback) {
        mOnInsetsCallback = onInsetsCallback;
    }

    public interface OnInsetsCallback {

        void onInsetsChanged(Rect insets);
    }
}
