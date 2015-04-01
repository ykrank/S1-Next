package cl.monsoon.s1next.util;

import org.joda.time.LocalDate;

public final class DateUtil {

    private DateUtil() {

    }

    /**
     * Used to construct {@link com.bumptech.glide.signature.StringSignature}s
     * to invalidate avatar every day.
     */
    public static String today() {
        return new LocalDate().toString();
    }

    public static String dayOfWeek() {
        return new LocalDate().dayOfWeek().withMinimumValue().toString();
    }

    public static String dayOfMonth() {
        return new LocalDate().dayOfMonth().withMinimumValue().toString();
    }
}
