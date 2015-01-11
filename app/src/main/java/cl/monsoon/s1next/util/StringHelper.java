package cl.monsoon.s1next.util;

public final class StringHelper {

    static final String TWO_SPACES = "  ";

    private StringHelper() {

    }

    public static String concatWithTwoSpaces(CharSequence title, int value) {
        return title + TWO_SPACES + value;
    }

    public static String concatWithTwoSpaces(CharSequence title, CharSequence text) {
        return title + TWO_SPACES + text;
    }
}
