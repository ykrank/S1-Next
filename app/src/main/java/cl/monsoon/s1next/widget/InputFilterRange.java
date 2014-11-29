package cl.monsoon.s1next.widget;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Users can't enter a value which is out of the range in EditView.
 */
public final class InputFilterRange implements InputFilter {

    private final int mMin;
    private final int mMax;

    public InputFilterRange(int min, int max) {
        if (min > max) {
            throw new IllegalStateException("Min can't larger than max.");
        }

        this.mMin = min;
        this.mMax = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String value =
                dest.subSequence(0, dstart).toString()
                        + source.subSequence(start, end)
                        + dest.subSequence(dend, dest.length());
        try {
            int input = Integer.parseInt(value);

            if (isInRange(input, mMin, mMax)) {
                return null;
            }
        } catch (NumberFormatException ignored) {

        }

        return "";
    }

    private boolean isInRange(int input, int min, int max) {
        return input >= min && input <= max;
    }
}
