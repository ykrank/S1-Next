package cl.monsoon.s1next.util;

public final class ObjectUtil {

    @SuppressWarnings("unchecked")
    public static <T> T uncheckedCast(Object obj) {
        return (T) obj;
    }

    public static <T> T cast(Object obj, Class<T> clazz) {
        if (obj.getClass().isAssignableFrom(clazz)) {
            return uncheckedCast(obj);
        } else {
            throw new ClassCastException(obj + " must extends " + clazz + ".");
        }
    }
}
