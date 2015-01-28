package cl.monsoon.s1next.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtil {

    private DateUtil() {

    }

    /**
     * Used to construct a {@link com.bumptech.glide.signature.StringSignature}
     * in {@link com.bumptech.glide.DrawableRequestBuilder#signature(com.bumptech.glide.load.Key)}
     * to invalidate avatar every day.
     */
    public static String getDayWithYear() {
        // y year, D day in year
        return new SimpleDateFormat("y D", Locale.US).format(new Date());
    }

    public static String getWeekWithYear() {
        // y year, w week in year
        return new SimpleDateFormat("y w", Locale.US).format(new Date());
    }
}
