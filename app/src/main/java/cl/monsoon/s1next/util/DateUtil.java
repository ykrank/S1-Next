package cl.monsoon.s1next.util;

import org.joda.time.LocalDate;

public final class DateUtil {

    private DateUtil() {

    }

    /**
     * Used to construct a {@link com.bumptech.glide.signature.StringSignature}
     * in {@link com.bumptech.glide.DrawableRequestBuilder#signature(com.bumptech.glide.load.Key)}
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
