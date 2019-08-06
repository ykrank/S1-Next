package me.ykrank.s1next.widget.span;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.github.ykrank.androidtools.util.L;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.R;
import me.ykrank.s1next.util.IntentUtil;

/**
 * A movement method that provides selection and clicking on links,
 * also invokes {@link ImageClickableResizeSpan}'s clicking event.
 */
public class PostMovementMethod extends ArrowKeyMovementMethod {

    private static PostMovementMethod sInstance;

    private List<URLSpanClick> urlSpanClicks = new ArrayList<>();

    private URLSpanClick defaultURLSpanClick;

    protected PostMovementMethod() {
        this(new DefaultURLSpanClick());
    }

    protected PostMovementMethod(@NonNull URLSpanClick defaultURLSpanClick) {
        this.defaultURLSpanClick = defaultURLSpanClick;
    }

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new PostMovementMethod();
            sInstance.addURLSpanClick(new SarabaSpan());
            sInstance.addURLSpanClick(new BilibiliSpan());
        }

        return sInstance;
    }

    private static boolean isSelecting(Spannable buffer) {
        return Selection.getSelectionStart(buffer) != -1 && Selection.getSelectionEnd(buffer) != -1;
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
                    ImageClickableResizeSpan[] imageClickableSpans = buffer.getSpans(off, off,
                            ImageClickableResizeSpan.class);
                    if (imageClickableSpans.length != 0) {
                        Context context = widget.getContext();
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setItems(new CharSequence[]{context.getString(R.string.show_image), context.getString(R.string.go_url)}, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                showImage(widget, buffer, line, off, imageClickableSpans);
                                                break;
                                            case 1:
                                                goUrl(widget, link);
                                                break;
                                        }
                                    }
                                }).create();
                        dialog.show();
                    } else {
                        goUrl(widget, link);
                    }
                } else {
                    //http://stackoverflow.com/questions/15836306/can-a-textview-be-selectable-and-contain-links
                    //error: Error when selecting text from Textview (java.lang.IndexOutOfBoundsException: setSpan (-1 ... -1) starts before 0)
                    if (!isSelecting(buffer)) {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }
                }
                return true;
            }

            // invoke ImageClickableResizeSpan's clicking event
            ImageClickableResizeSpan[] imageClickableSpans = buffer.getSpans(off, off,
                    ImageClickableResizeSpan.class);
            if (imageClickableSpans.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    showImage(widget, buffer, line, off, imageClickableSpans);
                } else {
                    if (!isSelecting(buffer)) {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(imageClickableSpans[0]),
                                buffer.getSpanEnd(imageClickableSpans[0]));
                    }
                }
                return true;
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    private void showImage(@NonNull TextView widget, @NonNull Spannable buffer, int line, int off,
                           @NonNull ImageClickableResizeSpan[] imageClickableSpans) {
        if (imageClickableSpans.length > 1) {
            //if use getSpans(off, off , sometime click mid in two span will cause error
            ImageClickableResizeSpan[] spans = buffer.getSpans(off, off + 1,
                    ImageClickableResizeSpan.class);
            if (spans.length == 1) {
                spans[0].onClick(widget);
                return;
            }
            L.report(new IllegalStateException("ImageClickableResizeSpan length warn; \n" +
                    "length" + imageClickableSpans.length + ",line:" + line + ",off:" + off
                    + ",newLength:" + spans.length));
        }
        imageClickableSpans[0].onClick(widget);
        return;
    }

    private void goUrl(@NonNull TextView widget, ClickableSpan[] link) {
        ClickableSpan clickableSpan = link[0];
        if (clickableSpan instanceof URLSpan) {
            Uri uri = Uri.parse(((URLSpan) clickableSpan).getURL());
            for (URLSpanClick click : urlSpanClicks) {
                if (click.isMatch(uri)) {
                    click.onClick(uri, widget);
                    return;
                }
            }
            defaultURLSpanClick.onClick(uri, widget);
        } else {
            clickableSpan.onClick(widget);
        }
        return;
    }

    public void addURLSpanClick(URLSpanClick click) {
        if (!urlSpanClicks.contains(click)) {
            urlSpanClicks.add(click);
        }
    }

    public interface URLSpanClick {
        /**
         * whether uri use this click listener
         */
        boolean isMatch(Uri uri);

        void onClick(Uri uri, View view);
    }

    public static class DefaultURLSpanClick implements URLSpanClick {

        @Override
        public boolean isMatch(Uri uri) {
            return true;
        }

        @Override
        public void onClick(Uri uri, View v) {
            // support Custom Tabs for URLSpan
            // see URLSpan#onClick(View)
            Context context = v.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            IntentUtil.putCustomTabsExtra(intent);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                L.e("URLSpan", "Activity was not found for intent, " + intent.toString());
            }
        }
    }
}
