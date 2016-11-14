package me.ykrank.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.URLSpan;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.model.Forum;
import me.ykrank.s1next.data.api.model.PmGroup;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.event.QuoteEvent;
import me.ykrank.s1next.data.pref.ThemeManager;
import me.ykrank.s1next.util.ResourceUtil;
import me.ykrank.s1next.util.ViewUtil;
import me.ykrank.s1next.widget.EventBus;
import me.ykrank.s1next.widget.span.GlideImageGetter;
import me.ykrank.s1next.widget.span.TagHandler;

public final class TextViewBindingAdapter {
    private static int defaultTextColor;

    private TextViewBindingAdapter() {
    }

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
    public static void loadTextAsset(TextView textView, String textPath) {
        try {
            InputStream inputStream = textView.getContext().getAssets().open(textPath);
            textView.setText(CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8)));
        } catch (IOException e) {
            throw new IllegalStateException("Can't find license.", e);
        }
    }

    @BindingAdapter({"forum", "gentleAccentColor"})
    public static void setForum(TextView textView, Forum forum, int gentleAccentColor) {
        textView.setText(forum.getName());
        // add today's posts count to each forum
        if (forum.getTodayPosts() != 0) {
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView, String.valueOf(forum.getTodayPosts()),
                    gentleAccentColor);
        }
    }

    @BindingAdapter({"themeManager", "thread", "user"})
    public static void setThread(TextView textView, ThemeManager themeManager, Thread thread, User user) {
        textView.setText(thread.getTitle());
        if (thread.getPermission() != 0) {
            // add thread's permission hint
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView,
                    "[" + textView.getContext().getString(R.string.thread_permission_hint)
                            + thread.getPermission() + "]");
        }
        if (thread.isHide()) {
            // add thread's permission hint
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView,
                    "[" + textView.getContext().getString(R.string.user_in_blacklist) + "]");
            textView.setTextColor(Color.GRAY);
        }else {
            textView.setTextColor(ResourceUtil.getTextColorPrimary(textView.getContext()));
        }
        // disable TextView if user has no permission to access this thread
        boolean hasPermission = user.getPermission() >= thread.getPermission();
        textView.setEnabled(hasPermission);

        // add thread's replies count to each thread
        ViewUtil.concatWithTwoSpacesForRtlSupport(textView, String.valueOf(thread.getReplies()),
                hasPermission ? themeManager.getGentleAccentColor()
                        : themeManager.getHintOrDisabledGentleAccentColor());
    }

    @BindingAdapter("relativeDateTime")
    public static void setRelativeDateTime(TextView textView, long datetime) {
        textView.setText(DateUtils.getRelativeDateTimeString(textView.getContext(), datetime,
                DateUtils.MINUTE_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));
    }

    @BindingAdapter({"eventBus", "post"})
    public static void setCount(TextView textView, EventBus eventBus, Post post) {
        String text = "#" + post.getCount();
        // there is no need to post #1
        if ("1".equals(post.getCount())) {
            textView.setText(text);
        } else {
            Spannable spannable = new SpannableString(text);
            URLSpan urlSpan = new URLSpan(StringUtils.EMPTY) {
                @Override
                public void onClick(@NonNull View widget) {
                    eventBus.post(new QuoteEvent(post.getId(), post.getCount()));
                }
            };
            spannable.setSpan(urlSpan, 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannable);
        }
    }

    @BindingAdapter({"reply", "hide"})
    public static void setReply(TextView textView, @Nullable String reply, boolean hide) {
        if (hide) {
            textView.setText("");
            // add thread's permission hint
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView,
                    "[" + textView.getContext().getString(R.string.user_in_blacklist) + "]");
            return;
        }
        if (TextUtils.isEmpty(reply)) {
            textView.setText(null);
        } else {
            // use GlideImageGetter to show images in TextView
            //noinspection deprecation
            textView.setText(Html.fromHtml(reply, GlideImageGetter.get(textView), new TagHandler()));
        }
    }

    @BindingAdapter({"search"})
    public static void setSearch(TextView textView, @Nullable String search) {
        if (TextUtils.isEmpty(search)) {
            textView.setText(null);
        } else {
            // use GlideImageGetter to show images in TextView
            //noinspection deprecation
            textView.setText(Html.fromHtml(search, GlideImageGetter.get(textView), new TagHandler()));
        }
    }

    @BindingAdapter({"pmMessage"})
    public static void setPm(TextView textView, @Nullable String pmMessage) {
        if (TextUtils.isEmpty(pmMessage)) {
            textView.setText(null);
        } else {
            // use GlideImageGetter to show images in TextView
            //noinspection deprecation
            textView.setText(Html.fromHtml(pmMessage, GlideImageGetter.get(textView), new TagHandler()));
        }
    }

    @SuppressWarnings("deprecation")
    @BindingAdapter({"pmAuthorNameDesc", "user"})
    public static void setPmAuthorNameDesc(TextView textView, PmGroup pmGroup, User user) {
        Context context = textView.getContext();
        if (TextUtils.equals(pmGroup.getLastAuthorid(), user.getUid())) {
            textView.setText(Html.fromHtml(context.getString(R.string.pm_desc_to_other, pmGroup.getToUsername())));
        } else {
            textView.setText(Html.fromHtml(context.getString(R.string.pm_desc_to_me, pmGroup.getToUsername())));
        }
    }
}
