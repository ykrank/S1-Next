package com.github.ykrank.androidtools.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtil {

    private DateUtil() {
    }

    /**
     * Used to construct {@link com.bumptech.glide.signature.StringSignature}s
     * to invalidate avatar every day.
     */
    public static String today() {
        return getSimpleDateFormatInstance().format(new Date());
    }

    /**
     * Used to construct {@link com.bumptech.glide.signature.StringSignature}s
     * to invalidate avatar every week.
     */
    public static String dayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return getSimpleDateFormatInstance().format(calendar.getTime());
    }

    /**
     * Used to construct {@link com.bumptech.glide.signature.StringSignature}s
     * to invalidate avatar every month.
     */
    public static String dayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getSimpleDateFormatInstance().format(calendar.getTime());
    }

    /**
     * Initialization on Demand Holder.
     * <p>
     * See https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
     */
    private static synchronized SimpleDateFormat getSimpleDateFormatInstance() {
        return SimpleDateFormatHolder.INSTANCE;
    }

    private static final class SimpleDateFormatHolder {

        private static final String TEMPLATE = "yyyy-MM-dd";

        private static final SimpleDateFormat INSTANCE = new SimpleDateFormat(TEMPLATE, Locale.getDefault());
    }
}
