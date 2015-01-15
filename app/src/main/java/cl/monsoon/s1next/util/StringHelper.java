package cl.monsoon.s1next.util;

import org.apache.commons.lang3.StringUtils;

public final class StringHelper {

    static final String TWO_SPACES = "  ";

    private static final String NON_BREAKING_SPACE_ENTITY_NAME = "&nbsp;";

    private StringHelper() {

    }

    public static String concatWithTwoSpaces(CharSequence title, int value) {
        return title + TWO_SPACES + value;
    }

    public static String concatWithTwoSpaces(CharSequence title, CharSequence text) {
        return title + TWO_SPACES + text;
    }

    public static String unescapeNonBreakingSpace(String value) {
        return StringUtils.replace(value, NON_BREAKING_SPACE_ENTITY_NAME, StringUtils.SPACE);
    }
}
