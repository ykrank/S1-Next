package cl.monsoon.s1next.util;

public final class StringHelper {

    private StringHelper() {

    }

    public static String concatTitleWithPageNum(CharSequence title, int pageNum) {
        return title + Util.TWO_SPACES + pageNum;
    }

    public static final class Util {

        public static final String TWO_SPACES = "  ";
        private static final String ELLIPSIS = "…";

        private Util() {

        }

        public static String ellipsize(String s, int maxLength) {
            if (s == null || s.length() < maxLength) {
                return s;
            }

            return s.substring(0, maxLength) + ELLIPSIS;
        }
    }
}
