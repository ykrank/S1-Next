package me.ykrank.s1next.util;

import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

public final class StringUtil {

    static final String TWO_SPACES = "  ";

    private static final String NON_BREAKING_SPACE_ENTITY_NAME = "&nbsp;";

    private StringUtil() {}

    /**
     * Concatenates {@code first} and {@code second} with {@link #TWO_SPACES}.
     * <p>
     * <pre>
     * StringUtil.concatWithTwoSpaces("a", 1) = "a  1"
     * </pre>
     *
     * @return A new string which is the concatenation of this string, two spaces
     * and the specified string.
     */
    public static String concatWithTwoSpaces(CharSequence first, int last) {
        return concatWithTwoSpaces(first, String.valueOf(last));
    }

    /**
     * <pre>
     * StringUtil.concatWithTwoSpaces(1, "a") = "1  a"
     * </pre>
     *
     * @see #concatWithTwoSpaces(CharSequence, int)
     */
    public static String concatWithTwoSpaces(int first, CharSequence last) {
        return concatWithTwoSpaces(String.valueOf(first), last);
    }

    /**
     * <pre>
     * StringUtil.concatWithTwoSpaces("a", "b") = "a  b"
     * </pre>
     *
     * @see #concatWithTwoSpaces(CharSequence, int)
     */
    public static String concatWithTwoSpaces(@Nullable CharSequence first, CharSequence last) {
        if (first == null) {
            return last.toString();
        }
        return first + TWO_SPACES + last;
    }

    /**
     * Replaces all occurrences of the {@link #NON_BREAKING_SPACE_ENTITY_NAME}
     * within the space.
     *
     * @param text The text to search and replace in.
     * @return The text with any replacements processed.
     */
    public static String unescapeNonBreakingSpace(String text) {
        return StringUtils.replace(text, NON_BREAKING_SPACE_ENTITY_NAME,
                StringUtils.SPACE);
    }
}
