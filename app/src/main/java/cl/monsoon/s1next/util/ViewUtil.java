package cl.monsoon.s1next.util;

import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Method;

public final class ViewUtil {

    private ViewUtil() {

    }

    /**
     * Concatenates with the specified text (two spaces and appendix) to the TextView.
     *
     * @param text the String that is concatenated to the TextView
     */
    public static void concatWithTwoSpacesForRtlSupport(TextView textView, CharSequence text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && textView.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            textView.append(StringUtil.TWO_SPACES + text, 0, 0);
        } else {
            textView.append(StringUtil.TWO_SPACES + text);
        }
    }

    /**
     * Concatenates with the specified text (two spaces and appendix) to the TextView.
     *
     * @param text      the String that is concatenated to the TextView
     * @param textColor the <code>text</code> color
     */
    public static void concatWithTwoSpacesForRtlSupport(TextView textView, CharSequence text, @ColorInt int textColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && textView.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            textView.setText(text + StringUtil.TWO_SPACES + textView.getText());
            ViewUtil.setForegroundColor(textView, textColor, 0, text.length());
        } else {
            int start = textView.length();
            textView.append(StringUtil.TWO_SPACES + text);
            ViewUtil.setForegroundColor(textView, textColor, start, textView.length());
        }
    }

    private static void setForegroundColor(TextView textView, int color, int start, int end) {
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(textView.getText());
        spannable.setSpan(new ForegroundColorSpan(color), start, end,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        textView.setText(spannable);
    }

    /**
     * @see android.widget.EditText#setShowSoftInputOnFocus(boolean)
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
