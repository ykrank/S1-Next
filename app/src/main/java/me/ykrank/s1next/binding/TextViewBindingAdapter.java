package me.ykrank.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.TextAppearanceSpan;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.TextView;

import com.github.ykrank.androidtools.util.L;
import com.github.ykrank.androidtools.util.ResourceUtil;
import com.github.ykrank.androidtools.util.RxJavaUtil;
import com.github.ykrank.androidtools.util.ViewUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import io.reactivex.Single;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.app.model.AppPost;
import me.ykrank.s1next.data.api.model.Forum;
import me.ykrank.s1next.data.api.model.HomeThread;
import me.ykrank.s1next.data.api.model.PmGroup;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.db.dbmodel.History;
import me.ykrank.s1next.data.pref.ThemeManager;
import me.ykrank.s1next.widget.span.GlideImageGetter;
import me.ykrank.s1next.widget.span.HtmlCompat;
import me.ykrank.s1next.widget.span.TagHandler;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

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

    @BindingAdapter("underlineText")
    public static void setUnderlineText(TextView textView, String text) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.getPaint().setAntiAlias(true);
        textView.setText(text);
    }

    @BindingAdapter("textPath")
    public static void loadTextAsset(TextView textView, String textPath) {
        try {
            InputStream inputStream = textView.getContext().getAssets().open(textPath);
            Source source = Okio.source(inputStream);
            BufferedSource bufferedSource = Okio.buffer(source);
            textView.setText(bufferedSource.readString(Charset.forName("utf-8")));
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

    @BindingAdapter({"themeManager", "forumId", "thread", "user"})
    public static void setThread(TextView textView, ThemeManager themeManager, String forumId, Thread thread, User user) {
        SpannableStringBuilder builder = new SpannableStringBuilder(thread.getTitle());
        TextAppearanceSpan hintSpan = new TextAppearanceSpan(textView.getContext(), R.style.TextAppearance_ThreadList_Title_Hint);
        if (thread.getPermission() != 0) {
            Spannable permSpan = new SpannableString(String.format("[%s%s]", textView.getContext().getString(R.string.thread_permission_hint),
                    thread.getPermission()));
            permSpan.setSpan(hintSpan, 0, permSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // add thread's permission hint
            builder.append(permSpan);
        }

        if (thread.isHide()) {
            Spannable blacklistSpan = new SpannableString(String.format("[%s]", textView.getContext().getString(R.string.user_in_blacklist)));
            blacklistSpan.setSpan(hintSpan, 0, blacklistSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // add thread's blacklist hint
            builder.append(blacklistSpan);
            textView.setTextColor(Color.GRAY);
        } else {
            textView.setTextColor(ResourceUtil.getTextColorPrimary(textView.getContext()));
        }
        // disable TextView if user has no permission to access this thread
        boolean hasPermission = user.getPermission() >= thread.getPermission();
        textView.setEnabled(hasPermission);

        //add typename
        if (!TextUtils.isEmpty(thread.getTypeName())) {
            Spannable typeSpan = new SpannableString(String.format("[%s] ", thread.getTypeName()));
            typeSpan.setSpan(hintSpan, 0, typeSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.insert(0, typeSpan);
        } else if (("0".equals(thread.getTypeId()) || "344".equals(thread.getTypeId())) && "75".equals(forumId) && thread.getDisplayOrder() == 0) {
            //add 泥潭
            Spannable typeSpan = new SpannableString("[泥潭] ");
            typeSpan.setSpan(hintSpan, 0, typeSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.insert(0, typeSpan);
        }

        textView.setText(builder);

        // add thread's replies count to each thread
        String repliesStr = thread.getReplies();
        if (thread.getReliesCount() > 0 && thread.getLastReplyCount() > 0) {
            int addReplies = thread.getReliesCount() - thread.getLastReplyCount();
            if (addReplies >= 0) {
                repliesStr += " (+" + addReplies + ")";
            } else {
                repliesStr += " (" + addReplies + ")";
            }
        }
        ViewUtil.concatWithTwoSpacesForRtlSupport(textView, repliesStr,
                hasPermission ? themeManager.getGentleAccentColor()
                        : themeManager.getHintOrDisabledGentleAccentColor());
    }

    @BindingAdapter("relativeDateTime")
    public static void setRelativeDateTime(TextView textView, long datetime) {
        textView.setText(DateUtils.getRelativeDateTimeString(textView.getContext(), datetime,
                DateUtils.MINUTE_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));
    }

    @BindingAdapter({"reply"})
    public static void setReply(TextView textView, AppPost post) {
        if (post == null) {
            return;
        }
        if (post.getHide()) {
            textView.setText("");
            String text = "[" + textView.getContext().getString(R.string.user_in_blacklist) + "]";
            if (!TextUtils.isEmpty(post.getRemark())) {
                text += "-[" + post.getRemark() + "]";
            }
            // add reply's blacklist hint
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView, text);
            return;
        }

        setHtmlWithImage(textView, post.getMessage());
    }

    @BindingAdapter({"reply"})
    public static void setReply(TextView textView, Post post) {
        if (post == null) {
            return;
        }
        if (post.getHide() != Post.Hide_Normal) {
            textView.setText("");
            String textHide;
            if (post.getHide() == Post.Hide_User) {
                textHide = textView.getContext().getString(R.string.user_in_blacklist);
            } else {
                textHide = textView.getContext().getString(R.string.word_in_black_word);
            }
            String text = "[" + textHide + "]";
            if (!TextUtils.isEmpty(post.getRemark())) {
                text += "-[" + post.getRemark() + "]";
            }
            // add reply's blacklist hint
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView, text);
            return;
        }
        if (post.isTrade()) {
            setHtmlWithImage(textView, post.getExtraHtml());
        } else {
            setHtmlWithImage(textView, post.getReply());
        }
    }

    @BindingAdapter({"imgHtml"})
    public static void setHtmlWithImage(TextView textView, @Nullable String html) {
        if (TextUtils.isEmpty(html)) {
            textView.setText(null);
        } else {
            // use GlideImageGetter to show images in TextView
            //noinspection deprecation
            Single.just(GlideImageGetter.Companion.get(textView))
                    .map(f -> HtmlCompat.fromHtml(html, f, new TagHandler()))
                    .compose(RxJavaUtil.computationSingleTransformer())
                    .subscribe(textView::setText, L::report);
        }
    }

    @BindingAdapter({"html"})
    public static void setHtml(TextView textView, @Nullable String html) {
        if (TextUtils.isEmpty(html)) {
            textView.setText(null);
        } else {
            //noinspection deprecation
            Single.fromCallable(() -> HtmlCompat.fromHtml(html))
                    .compose(RxJavaUtil.computationSingleTransformer())
                    .subscribe(textView::setText, L::report);
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

    @BindingAdapter({"homeThread"})
    public static void setHomeThread(TextView textView, @Nullable HomeThread thread) {
        if (thread == null) {
            textView.setText(null);
        } else {
            textView.setText(thread.getTitle());
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView, thread.getForum(), Color.GRAY);
        }
    }

    @BindingAdapter({"history"})
    public static void setHomeThread(TextView textView, @Nullable History history) {
        if (history == null) {
            textView.setText(null);
        } else {
            textView.setText(history.getTitle());
        }
    }
}
