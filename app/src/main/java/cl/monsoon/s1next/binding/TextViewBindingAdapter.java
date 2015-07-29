package cl.monsoon.s1next.binding;

import android.databinding.BindingAdapter;
import android.text.method.MovementMethod;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.model.Forum;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.util.ViewUtil;

public final class TextViewBindingAdapter {

    @BindingAdapter("movementMethod")
    public static void setMovementMethod(TextView textView, MovementMethod movementMethod) {
        textView.setMovementMethod(movementMethod);
    }

    @BindingAdapter("filePath")
    public static void loadLicense(TextView textView, String filePath) {
        try {
            InputStream inputStream = textView.getContext().getAssets().open(filePath);
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
}
