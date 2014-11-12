package com.statemachinesystems.envy;

import java.util.HashMap;
import java.util.Map;

public class Conversions {

    private static final Map<Class<?>, Class<?>> primitiveToBoxed = new HashMap<Class<?>, Class<?>>();

    static {
        primitiveToBoxed.put(boolean.class, Boolean.class);
        primitiveToBoxed.put(byte.class, Byte.class);
        primitiveToBoxed.put(char.class, Character.class);
        primitiveToBoxed.put(double.class, Double.class);
        primitiveToBoxed.put(float.class, Float.class);
        primitiveToBoxed.put(int.class, Integer.class);
        primitiveToBoxed.put(long.class, Long.class);
        primitiveToBoxed.put(short.class, Short.class);
        primitiveToBoxed.put(void.class, Void.class);
    }

    public static Class<?> toBoxed(Class<?> c) {
        return c.isPrimitive()
                ? primitiveToBoxed.get(c)
                : c;
    }

    private Conversions() {}
}
