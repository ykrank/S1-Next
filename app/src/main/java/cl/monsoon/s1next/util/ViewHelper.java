package cl.monsoon.s1next.util;

import android.support.annotation.ColorRes;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.widget.TextView;

import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.singleton.Config;

public final class ViewHelper {

    private ViewHelper() {

    }

    public static void concatWithTwoSpaces(TextView textView, int text) {
        concatWithTwoSpaces(textView, String.valueOf(text));
    }

    public static void concatWithTwoSpaces(TextView textView, String text) {
        textView.append(StringHelper.TWO_SPACES + text);
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

    public static void updateTextColorWhenS1Theme(TextView... textViewList) {
        if (Config.isS1Theme()) {
            int color =
                    MyApplication.getContext()
                            .getResources().getColor(R.color.s1_theme_text_color_primary_87);
            for (TextView textView : textViewList) {
                textView.setTextColor(color);
            }
        }
    }
}
