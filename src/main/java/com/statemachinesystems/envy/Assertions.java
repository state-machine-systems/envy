package com.statemachinesystems.envy;

import java.lang.reflect.Method;

/**
 * Helper class for asserting reflection pre-conditions.
 */
public class Assertions {

    public static void assertInterface(Class<?> c) {
        if (! c.isInterface()) {
            throw new IllegalArgumentException(
                    String.format("%s is not an interface", c.getCanonicalName()));
        }
    }

    public static void assertArray(Class<?> c) {
        if (! c.isArray()) {
            throw new IllegalArgumentException(
                    String.format("%s is not an array", c.getCanonicalName()));
        }
    }

    public static void assertEnum(Class<?> c) {
        if (! c.isEnum()) {
            throw new IllegalArgumentException(
                    String.format("%s is not an enum", c.getCanonicalName()));
        }
    }

    public static void assertMethodWithNoParameters(Method m) {
        if (m.getParameterTypes().length > 0) {
            throw new IllegalArgumentException(
                    String.format("%s must not take any parameters", m.getName()));
        }
    }

    public static void assertMethodWithNonVoidReturnType(Method m) {
        if (Conversions.toBoxed(m.getReturnType()).equals(Void.class)) {
            throw new IllegalArgumentException(
                    String.format("%s must not have a void return type", m.getName()));

        }
    }

    public static void assertNotObjectMethod(Method m) {
        Method objectMethod;
        try {
            objectMethod = Object.class.getMethod(m.getName());
        } catch (NoSuchMethodException e) {
            objectMethod = null;
        }

        if (objectMethod != null && objectMethod.getReturnType().equals(m.getReturnType())) {
            throw new IllegalArgumentException(
                    String.format("Illegal overridden java.lang.Object method: %s", m.getName()));
        }
    }

    private Assertions() {}
}
