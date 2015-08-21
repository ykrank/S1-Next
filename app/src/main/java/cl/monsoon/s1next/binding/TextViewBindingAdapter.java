package cl.monsoon.s1next.binding;

import android.databinding.BindingAdapter;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.model.Forum;
import cl.monsoon.s1next.data.api.model.Post;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.data.event.QuoteEvent;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.util.ViewUtil;
import cl.monsoon.s1next.widget.EventBus;
import cl.monsoon.s1next.widget.GlideImageGetter;
import cl.monsoon.s1next.widget.TagHandler;

public final class TextViewBindingAdapter {

    private TextViewBindingAdapter() {}

    @BindingAdapter("increaseClickingArea")
    public static void increaseClickingArea(TextView textView, float size) {
        // fork from http://stackoverflow.com/a/1343796
        View parent = (View) textView.getParent();
        // post in the parent's message queue to make sure the parent
        // lays out its children before we call View#getHitRect()
        parent.post(() -> {
            final int halfSize = (int) (size / 2 + 0.5);
            Rect rect = new Rect();
            textView.getHitRect(rect);
            rect.top -= halfSize;
            rect.right += halfSize;
            rect.bottom += halfSize;
            rect.left -= halfSize;
            // use TouchDelegate to increase count's clicking area
            parent.setTouchDelegate(new TouchDelegate(rect, textView));
        });
    }

    @BindingAdapter("textPath")
    public static void loadText(TextView textView, String textPath) {
        try {
            InputStream inputStream = textView.getContext().getAssets().open(textPath);
            textView.setText(CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8)));
        } catch (IOException e) {
            throw new IllegalStateException("Can't find license.", e);
        }
    }

    @BindingAdapter({"forum", "gentleAccentColor"})
    public static void showForum(TextView textView, Forum forum, int gentleAccentColor) {
        textView.setText(forum.getName());
        // add today's posts count to each forum
        if (forum.getTodayPosts() != 0) {
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView, String.valueOf(forum.getTodayPosts()),
                    gentleAccentColor);
        }
    }

    @BindingAdapter({"themeManager", "thread", "user"})
    public static void setText(TextView textView, ThemeManager themeManager, Thread thread, User user) {
        textView.setText(thread.getTitle());
        if (thread.getPermission() != 0) {
            // add thread's permission hint
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView,
                    "[" + textView.getContext().getString(R.string.thread_permission_hint)
                            + thread.getPermission() + "]");
        }
        // disable TextView if user has no permission to access this thread
        boolean hasPermission = user.getPermission() >= thread.getPermission();
        textView.setEnabled(hasPermission);

        // add thread's replies count to each thread
        ViewUtil.concatWithTwoSpacesForRtlSupport(textView, String.valueOf(thread.getReplies()),
                hasPermission ? themeManager.getGentleAccentColor()
                        : themeManager.getHintOrDisabledGentleAccentColor());
    }

    @BindingAdapter("datetime")
    public static void setText(TextView textView, long datetime) {
        textView.setText(DateUtils.getRelativeDateTimeString(textView.getContext(), datetime,
                DateUtils.MINUTE_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));
    }

    @BindingAdapter({"eventBus", "post"})
    public static void setText(TextView textView, EventBus eventBus, Post post) {
        // there is no need to quote #1
        if ("1".equals(post.getCount())) {
            textView.setText("#1");
            textView.setClickable(false);
            textView.setLongClickable(false);
            textView.setFocusable(false);
        } else {
            Spannable spannable = new SpannableString("#" + post.getCount());
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    eventBus.post(new QuoteEvent(post.getId(), post.getCount()));
                }
            };
            spannable.setSpan(clickableSpan, 0, spannable.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannable);
            textView.setClickable(true);
            textView.setLongClickable(true);
            textView.setFocusable(true);
        }
    }

    @BindingAdapter("reply")
    public static void setText(TextView textView, @Nullable String reply) {
        if (TextUtils.isEmpty(reply)) {
            textView.setText(null);
        } else {
            // use GlideImageGetter to show images in TextView
            textView.setText(Html.fromHtml(reply, new GlideImageGetter(textView.getContext(),
                    textView), new TagHandler()));
        }
    }
}
