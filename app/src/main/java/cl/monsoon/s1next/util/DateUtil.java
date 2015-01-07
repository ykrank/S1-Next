package cl.monsoon.s1next.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

    private DateUtil() {

    }

    public static String getDayWithYear() {
        // y year, D day in year
        return new SimpleDateFormat("y D").format(new Date());
    }

    public static String getWeekWithYear() {
        // y year, w week in year
        return new SimpleDateFormat("y w").format(new Date());
    }
}
