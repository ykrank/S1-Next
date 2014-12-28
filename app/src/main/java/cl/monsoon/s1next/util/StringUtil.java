package cl.monsoon.s1next.util;

public final class StringUtil {

    public static final String TWO_SPACES = "  ";

    public static String ellipsize(String s, int maxLength) {
        if (s == null || s.length() < maxLength) {
            return s;
        }

        return s.substring(0, maxLength) + "…";
    }
}
