package cl.monsoon.s1next.util;

import android.os.Build;
import android.support.annotation.ColorRes;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Method;

import cl.monsoon.s1next.singleton.Config;

public final class ViewUtil {

    private ViewUtil() {

    }

    public static void concatWithTwoSpaces(TextView textView, int text) {
        concatWithTwoSpaces(textView, String.valueOf(text));
    }

    public static void concatWithTwoSpaces(TextView textView, String text) {
        textView.append(StringUtil.TWO_SPACES + text);
    }

    public static void setForegroundColor(TextView textView, @ColorRes int color, int start, int end) {
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(textView.getText());
        spannable.setSpan(
                new ForegroundColorSpan(color),
                start,
                end,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        textView.setText(spannable);
    }

    /**
     * Updates the TextViews font size depends on
     * {@link cl.monsoon.s1next.singleton.Config#textScale}.
     *
     * @param textViewList also works for {@link android.widget.EditText} and {@link android.widget.Button}.
     */
    public static void updateTextSize(TextView... textViewList) {
        float textScale = Config.getTextScale();
        for (TextView textView : textViewList) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize() * textScale);
        }
    }

    /**
     * @see android.widget.EditText#setShowSoftInputOnFocus(boolean)
     */
    public static void setShowSoftInputOnFocus(EditText editText, Boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setShowSoftInputOnFocus(show);
        } else {
            try {
                Method method = EditText.class.getMethod("setShowSoftInputOnFocus", boolean.class);
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
