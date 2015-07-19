package cl.monsoon.s1next.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathUtil {

    private MathUtil() {

    }

    /**
     * Returns a new {@code BigDecimal} whose value is {@code divident / divisor}
     * with {@link RoundingMode#UP} mode.
     */
    public static int divide(int divident, int divisor) {
        return new BigDecimal(divident).divide(new BigDecimal(divisor), RoundingMode.UP).intValue();
    }
}
