package com.statemachinesystems.envy;

import java.lang.reflect.Method;

public class Assertions {

    public static void assertInterface(Class<?> c) {
        if (! c.isInterface()) {
            throw new IllegalArgumentException(
                    String.format("%s is not an interface", c.getName()));
        }
    }

    public static void assertEnum(Class<?> c) {
        if (! c.isEnum()) {
            throw new IllegalArgumentException(
                    String.format("%s is not an enum", c.getName()));
        }
    }

    public static void assertMethodWithNoParameters(Method m) {
        if (m.getParameterTypes().length > 0) {
            throw new IllegalArgumentException(
                    String.format("%s must not take any parameters", m.getName()));
        }
    }

    private Assertions() {}
}
