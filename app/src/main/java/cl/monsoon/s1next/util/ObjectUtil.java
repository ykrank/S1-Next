package cl.monsoon.s1next.util;

public class ObjectUtil {

    @SuppressWarnings("unchecked")
    public static <T> T uncheckedCast(Object obj) {
        return (T) obj;
    }
}
