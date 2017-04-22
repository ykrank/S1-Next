package me.ykrank.s1next.util;

import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

public final class StringUtil {

    static final String TWO_SPACES = "  ";

    private static final String NON_BREAKING_SPACE_ENTITY_NAME = "&nbsp;";

    private StringUtil() {
    }

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

    /**
     * decode like \u8652
     */
    public static String uniDecode(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\\' && chars[i + 1] == 'u') {
                char cc = 0;
                for (int j = 0; j < 4; j++) {
                    char ch = Character.toLowerCase(chars[i + 2 + j]);
                    if ('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f') {
                        cc |= (Character.digit(ch, 16) << (3 - j) * 4);
                    } else {
                        cc = 0;
                        break;
                    }
                }
                if (cc > 0) {
                    i += 5;
                    sb.append(cc);
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
