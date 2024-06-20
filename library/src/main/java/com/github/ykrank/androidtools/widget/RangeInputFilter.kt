package com.github.ykrank.androidtools.widget

import android.text.InputFilter
import android.text.Spanned
import android.util.Range

/**
 * Constrains input from entering a value which is out of the range.
 *
 *
 * Forked from http://stackoverflow.com/q/14212518
 */
class RangeInputFilter(lower: Int, upper: Int) : InputFilter {
    private val mRange: Range<Int>

    init {
        mRange = Range.create(lower, upper)
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val value = (dest.subSequence(0, dstart).toString()
                + source.subSequence(start, end)
                + dest.subSequence(dend, dest.length))
        try {
            if (mRange.contains(value.toInt())) {
                return null
            }
        } catch (ignored: NumberFormatException) {
        }
        return ""
    }
}
