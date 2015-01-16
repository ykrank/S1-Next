package cl.monsoon.s1next.util;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        return new SimpleDateFormat("y D").format(new Date());
    }

    /**
     * @see #getDayWithYear()
     */
    public static String getWeekWithYear() {
        // y year, w week in year
        return new SimpleDateFormat("y w").format(new Date());
    }
}
