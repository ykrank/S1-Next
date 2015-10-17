package cl.monsoon.s1next.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Method;

import cl.monsoon.s1next.R;

public final class ViewUtil {

    private ViewUtil() {}

    /**
     * Concatenates with the specified text (two spaces and appendix) to the TextView
     * with RTL support.
     *
     * @param text the String that is concatenated to the TextView
     */
    @SuppressLint("SetTextI18n")
    public static void concatWithTwoSpacesForRtlSupport(TextView textView, CharSequence text) {
        if (ResourceUtil.isRTL(textView.getResources())) {
            textView.setText(text + StringUtil.TWO_SPACES + textView.getText());
        } else {
            textView.append(StringUtil.TWO_SPACES + text);
        }
    }

    /**
     * Concatenates with the specified text (two spaces and appendix) to the TextView
     * with RTL support.
     *
     * @param text      the String that is concatenated to the TextView
     * @param textColor the <code>text</code> color
     */
    @SuppressLint("SetTextI18n")
    public static void concatWithTwoSpacesForRtlSupport(TextView textView, CharSequence text, @ColorInt int textColor) {
        if (ResourceUtil.isRTL(textView.getResources())) {
            textView.setText(text + StringUtil.TWO_SPACES + textView.getText());
            ViewUtil.setForegroundColor(textView, textColor, 0, text.length());
        } else {
            int start = textView.length();
            textView.append(StringUtil.TWO_SPACES + text);
            ViewUtil.setForegroundColor(textView, textColor, start, textView.length());
        }
    }

    /**
     * Sets foreground color for {@code textView}.
     */
    private static void setForegroundColor(TextView textView, int color, int start, int end) {
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(textView.getText());
        spannable.setSpan(new ForegroundColorSpan(color), start, end,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        textView.setText(spannable);
    }

    /**
     * Consumes a {@link Runnable} when an IME action in the {@link TextView} is performed.
     * This method uses {@link TextView#setOnEditorActionListener(TextView.OnEditorActionListener)}
     * to achieve this function.
     *
     * @param textView The view where an IME action is preformed on.
     * @param runnable The consumer when an IME action is performed.
     */
    public static void consumeRunnableWhenImeActionPerformed(TextView textView, Runnable runnable) {
        textView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == v.getResources().getInteger(R.integer.ime_action_id) ||
                    actionId == EditorInfo.IME_ACTION_DONE) {
                runnable.run();
            }
            return false;
        });
    }

    /**
     * Backports {@link TextView#setShowSoftInputOnFocus} to API 20 and below.
     *
     * @see TextView#setShowSoftInputOnFocus(boolean)
     */
    public static void setShowSoftInputOnFocus(EditText editText, Boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setShowSoftInputOnFocus(show);
        } else {
            try {
                Method method;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    method = TextView.class.getMethod("setShowSoftInputOnFocus", boolean.class);
                } else {
                    method = TextView.class.getMethod("setSoftInputShownOnFocus", boolean.class);
                }
                method.setAccessible(true);
                method.invoke(editText, show);
            } catch (Exception e) {
                // multi-catch with those reflection exceptions requires API level 19
                // so we use Exception instead of multi-catch
                throw new RuntimeException("Failed to invoke TextView#setShowSoftInputOnFocus(boolean).", e);
            }
        }
    }
}
