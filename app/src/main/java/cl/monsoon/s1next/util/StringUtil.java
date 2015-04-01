package cl.monsoon.s1next.util;

import org.apache.commons.lang3.StringUtils;

public final class StringUtil {

    static final String TWO_SPACES = "  ";

    private static final String NON_BREAKING_SPACE_ENTITY_NAME = "&nbsp;";

    private StringUtil() {

    }

    public static String concatWithTwoSpaces(CharSequence first, int last) {
        return concatWithTwoSpaces(first.toString(), String.valueOf(last));
    }

    public static String concatWithTwoSpaces(CharSequence first, CharSequence last) {
        return first + TWO_SPACES + last;
    }

    public static String unescapeNonBreakingSpace(CharSequence text) {
        return StringUtils.replace(
                text.toString(), NON_BREAKING_SPACE_ENTITY_NAME, StringUtils.SPACE);
    }
}
