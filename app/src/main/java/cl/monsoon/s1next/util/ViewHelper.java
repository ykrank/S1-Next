package cl.monsoon.s1next.util;

import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.widget.TextView;

import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.singleton.Config;

public final class ViewHelper {

    private ViewHelper() {

    }

    /**
     * Update TextView's font size depends on
     * {@link cl.monsoon.s1next.singleton.Config#textScale} (which same to Settings).
     *
     * @param textViewList also works for {@link android.widget.EditText} and {@link android.widget.Button}.
     */
    public static void updateTextSize(@NonNull TextView[] textViewList) {
        for (TextView textView : textViewList) {
            textView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    textView.getTextSize() * Config.getTextScale());
        }
    }

    public static void updateTextColorWhenS1Theme(@NonNull TextView[] textViewList) {
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
