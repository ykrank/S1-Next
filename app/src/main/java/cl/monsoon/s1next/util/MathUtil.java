package cl.monsoon.s1next.util;

public final class MathUtil {

    private MathUtil() {

    }

    public static int divide(int divident, int divisor) {
        if (divident <= 0) {
            throw new IllegalStateException("Divident can't less than or equal to 0.");
        }

        return (divident + divisor - 1) / divisor;
    }
}
