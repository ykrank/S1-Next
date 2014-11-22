package cl.monsoon.s1next.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * See https://android.googlesource.com/platform/frameworks/base/+/94c02a1a1a6d7e6900e5a459e9cc699b9510e5a2
 */
@SuppressWarnings("UnusedDeclaration")
final class ListPreferenceCompat extends ListPreference {

    public ListPreferenceCompat(Context context) {
        super(context);
    }

    public ListPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * setValue() was not calling notifyChanged() before android-4.4,
     */
    @Override
    public void setValue(String value) {
        super.setValue(value);

        notifyChanged();
    }
}
