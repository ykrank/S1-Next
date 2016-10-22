package me.ykrank.s1next.widget.span;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.util.IntentUtil;
import me.ykrank.s1next.util.L;

/**
 * A movement method that provides selection and clicking on links,
 * also invokes {@link TagHandler.ImageClickableSpan}'s clicking event.
 */
public class CustomMovementMethod extends ArrowKeyMovementMethod {

    private static CustomMovementMethod sInstance;

    private List<URLSpanClick> urlSpanClicks = new ArrayList<>();

    private URLSpanClick defaultURLSpanClick;

    protected CustomMovementMethod(){
        this(new DefaultURLSpanClick());
    }

    protected CustomMovementMethod(@NonNull URLSpanClick defaultURLSpanClick){
        this.defaultURLSpanClick = defaultURLSpanClick;
    }

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new CustomMovementMethod();
            sInstance.addURLSpanClick(new SarabaSpan());
            sInstance.addURLSpanClick(new BilibiliSpan());
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
                    ClickableSpan clickableSpan = link[0];
                    if (clickableSpan instanceof URLSpan) {
                        Uri uri = Uri.parse(((URLSpan) clickableSpan).getURL());
                        for (URLSpanClick click: urlSpanClicks) {
                            if (click.isMatch(uri)){
                                click.onClick(uri, widget);
                                return true;
                            }
                        }
                        defaultURLSpanClick.onClick(uri, widget);
                    } else {
                        clickableSpan.onClick(widget);
                    }
                } else {
                    return super.onTouchEvent(widget, buffer, event);
                }

                return true;
            }

            // invoke ImageClickableSpan's clicking event
            TagHandler.ImageClickableSpan[] imageClickableSpans = buffer.getSpans(off, off,
                    TagHandler.ImageClickableSpan.class);
            if (action == MotionEvent.ACTION_UP) {
                if (imageClickableSpans.length == 1) {
                    imageClickableSpans[0].onClick(widget);

                    return true;
                } else if (imageClickableSpans.length > 1) {
                    throw new IllegalStateException("ImageClickableSpan length > 1; \n" +
                            "length"+imageClickableSpans.length+",line:"+line+",off:"+off);
                }
            }

        }

        return super.onTouchEvent(widget, buffer, event);
    }

    public void addURLSpanClick(URLSpanClick click){
        if (!urlSpanClicks.contains(click)) {
            urlSpanClicks.add(click);
        }
    }

    public static class DefaultURLSpanClick implements URLSpanClick{

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

    public interface URLSpanClick{
        boolean isMatch(Uri uri);
        void onClick(Uri uri, View view);
    }
}
