package cl.monsoon.s1next.widget;

import android.text.InputFilter;
import android.text.Spanned;

import org.apache.commons.lang3.Range;

/**
 * Users can't enter a value which is out of the range in EditView.
 */
public final class InputFilterRange implements InputFilter {

    private final Range<Integer> mRange;

    public InputFilterRange(Range<Integer> range) {
        this.mRange = range;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String value =
                dest.subSequence(0, dstart).toString()
                        + source.subSequence(start, end)
                        + dest.subSequence(dend, dest.length());
        try {
            if (mRange.contains(Integer.valueOf(value))) {
                return null;
            }
        } catch (NumberFormatException ignored) {

        }

        return "";
    }
}
