package cl.monsoon.s1next.util;

public final class ObjectUtil {

    @SuppressWarnings("unchecked")
    public static <T> T uncheckedCast(Object obj) {
        return (T) obj;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj, Class<T> clazz) {
        if (clazz.isInstance(obj)) {
            return (T) obj;
        } else {
            throw new ClassCastException(obj + " must extend/implement " + clazz + ".");
        }
    }
}
