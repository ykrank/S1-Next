package me.ykrank.s1next.widget;

import android.text.InputFilter;
import android.text.Spanned;

import com.google.common.collect.Range;

import org.apache.commons.lang3.StringUtils;

/**
 * Constrains input from entering a value which is out of the range.
 * <p>
 * Forked from http://stackoverflow.com/q/14212518
 */
public final class RangeInputFilter implements InputFilter {

    private final Range<Integer> mRange;

    public RangeInputFilter(int lower, int upper) {
        this.mRange = Range.closed(lower, upper);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String value = dest.subSequence(0, dstart).toString()
                + source.subSequence(start, end)
                + dest.subSequence(dend, dest.length());
        try {
            if (mRange.contains(Integer.valueOf(value))) {
                return null;
            }
        } catch (NumberFormatException ignored) {

        }

        return StringUtils.EMPTY;
    }
}
