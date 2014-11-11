package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.lang.reflect.Constructor;

public class ReflectionValueParser<T> implements ValueParser<T> {

    public static <T> ReflectionValueParser<T> parserOrNull(Class<T> valueClass) {
        Constructor<T> constructor = stringConstructorOrNull(valueClass);
        return constructor != null
                ? new ReflectionValueParser<T>(constructor)
                : null;
    }

    public static <T> Constructor<T> stringConstructorOrNull(Class<T> targetClass) {
        @SuppressWarnings("unchecked")
        Constructor<T>[] constructors = (Constructor<T>[]) targetClass.getConstructors();

        for (Constructor<T> constructor : constructors) {
            if (isStringConstructor(constructor)) {
                return constructor;
            }
        }
        return null;
    }

    public static <T> boolean isStringConstructor(Constructor<T> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        return parameterTypes.length == 1
                && parameterTypes[0].isAssignableFrom(String.class);// TODO CharSequence instead?
    }

    private final Constructor<T> constructor;

    private ReflectionValueParser(Constructor<T> constructor) {
        this.constructor = constructor;
    }

    @Override
    public T parseValue(String value) {
        try {
            return constructor.newInstance(value);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Class<T> getValueClass() {
        return constructor.getDeclaringClass();
    }
}
