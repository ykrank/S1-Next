package cl.monsoon.s1next.widget;

import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Handle {@link cl.monsoon.s1next.widget.ImageTagHandler.ImageClickableSpan} click event.
 */
public final class ClickMovementMethod extends LinkMovementMethod {

    /**
     * @see android.text.method.LinkMovementMethod#onTouchEvent(android.widget.TextView, android.text.Spannable, android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(@NonNull TextView widget, @NonNull Spannable buffer, @NonNull MotionEvent event) {

        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ImageTagHandler.ImageClickableSpan[] imageClickableSpans =
                    buffer.getSpans(off, off, ImageTagHandler.ImageClickableSpan.class);
            if (imageClickableSpans.length != 0) {
                imageClickableSpans[0].onClick(widget);

                return true;
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new ClickMovementMethod();
        }

        return sInstance;
    }

    private static LinkMovementMethod sInstance;
}
