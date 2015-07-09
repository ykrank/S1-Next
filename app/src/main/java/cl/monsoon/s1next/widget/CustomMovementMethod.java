package cl.monsoon.s1next.widget;

import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * A movement method that provides selection and clicking on links,
 * also invokes {@link TagHandler.ImageClickableSpan}'s clicking event.
 */
public final class CustomMovementMethod extends ArrowKeyMovementMethod {

    private static CustomMovementMethod sInstance;

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new CustomMovementMethod();
        }

        return sInstance;
    }

    /**
     * @see android.text.method.LinkMovementMethod#onTouchEvent(TextView, Spannable, MotionEvent)
     */
    @Override
    public boolean onTouchEvent(@NonNull TextView widget, @NonNull Spannable buffer, @NonNull MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);
                } else {
                    Selection.setSelection(buffer, buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }

                return true;
            }

            // invoke ImageClickableSpan's clicking event
            TagHandler.ImageClickableSpan[] imageClickableSpans = buffer.getSpans(off, off,
                    TagHandler.ImageClickableSpan.class);
            if (imageClickableSpans.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    imageClickableSpans[0].onClick(widget);
                }

                return true;
            }

        }

        return super.onTouchEvent(widget, buffer, event);
    }
}
