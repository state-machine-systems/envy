package com.statemachinesystems.envy;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import static com.statemachinesystems.envy.Assertions.assertArray;

/**
 * Helper class for converting between primitive and boxed types.
 */
public class Conversions {

    private static final Map<Class<?>, Class<?>> primitiveToBoxed = new HashMap<Class<?>, Class<?>>();
    private static final Map<Class<?>, Class<?>> boxedToPrimitive = new HashMap<Class<?>, Class<?>>();

    static {
        registerBoxedClassForPrimitive(boolean.class, Boolean.class);
        registerBoxedClassForPrimitive(byte.class, Byte.class);
        registerBoxedClassForPrimitive(char.class, Character.class);
        registerBoxedClassForPrimitive(double.class, Double.class);
        registerBoxedClassForPrimitive(float.class, Float.class);
        registerBoxedClassForPrimitive(int.class, Integer.class);
        registerBoxedClassForPrimitive(long.class, Long.class);
        registerBoxedClassForPrimitive(short.class, Short.class);
        registerBoxedClassForPrimitive(void.class, Void.class);
    }

    private static void registerBoxedClassForPrimitive(Class<?> primitive, Class<?> boxed) {
        primitiveToBoxed.put(primitive, boxed);
        boxedToPrimitive.put(boxed, primitive);
    }

    public static Class<?> toBoxed(Class<?> c) {
        return c.isPrimitive()
                ? primitiveToBoxed.get(c)
                : c;
    }

    public static Class<?> toPrimitive(Class<?> c) {
        if (c.isPrimitive()) {
            return c;
        }
        Class<?> primitive = boxedToPrimitive.get(c);
        if (primitive == null) {
            throw new IllegalArgumentException(
                    String.format("%s is not a wrapper class", c.getSimpleName()));
        }
        return primitive;
    }

    public static Object boxedArrayToPrimitiveArray(Object src) {
        assertArray(src.getClass());

        int length = Array.getLength(src);
        Class<?> componentType = src.getClass().getComponentType();
        if (componentType.isPrimitive()) {
            return src;
        }

        Object dest = Array.newInstance(toPrimitive(componentType), length);
        for (int i = 0; i < length; i++) {
            Array.set(dest, i, Array.get(src, i));
        }
        return dest;
    }

    private Conversions() {}
}
