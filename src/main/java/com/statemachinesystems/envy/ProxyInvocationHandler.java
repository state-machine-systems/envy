package com.statemachinesystems.envy;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.statemachinesystems.envy.Assertions.assertMethodWithNoParameters;

/**
 * Handles method calls on proxied configuration interfaces.
 *
 * Method names and types on the interface are mapped to {@link com.statemachinesystems.envy.Parameter}s
 * and {@link com.statemachinesystems.envy.ValueParser}s.
 *
 * @see java.lang.reflect.InvocationHandler
 */
public class ProxyInvocationHandler implements InvocationHandler {

    public static ProxyInvocationHandler createInvocationHandler(
            Class<?> configClass,
            ConfigSource configSource,
            ValueParserFactory valueParserFactory) {

        Map<Method, Object> values = new LinkedHashMap<Method, Object>();

        for (Method method : configClass.getDeclaredMethods()) {
            assertMethodWithNoParameters(method);

            Parameter parameter = getParameter(method);
            String rawValue = getRawValue(configSource, parameter, configClass, method);
            Object parsedValue = parseValue(rawValue, valueParserFactory, configClass, method);

            values.put(method, parsedValue);
        }

        return new ProxyInvocationHandler(values);
    }

    private static Parameter getParameter(Method method) {
        Name customParameterName = method.getAnnotation(Name.class);
        return customParameterName != null
            ? new Parameter(customParameterName.value())
            : Parameter.fromMethodName(method.getName());
    }

    private static String getRawValue(ConfigSource configSource,
                                      Parameter parameter,
                                      Class<?> configClass,
                                      Method method) {

        String rawValue = configSource.getValue(parameter);
        if (rawValue == null) {
            rawValue = getDefaultValue(method);
        }
        if (rawValue == null && isMandatory(method)) {
            throw new IllegalArgumentException(
                    String.format("Missing configuration value for %s.%s",
                            configClass.getSimpleName(), method.getName()));
        }
        return rawValue;
    }

    private static String getDefaultValue(Method method) {
        Default defaultAnnotation = method.getAnnotation(Default.class);
        return defaultAnnotation != null
            ? defaultAnnotation.value()
            : null;
    }

    private static boolean isMandatory(Method method) {
        return method.getReturnType().isPrimitive()
            || method.getAnnotation(Optional.class) == null;
    }

    private static Object parseValue(String rawValue,
                                     ValueParserFactory valueParserFactory,
                                     Class<?> configClass,
                                     Method method) {
        if (rawValue == null) {
            return null;
        }

        ValueParser<?> valueParser = getValueParser(valueParserFactory, configClass, method);
        Object parsedValue = valueParser.parseValue(rawValue);

        return Conversions.isPrimitiveArray(method.getReturnType())
                ? Conversions.boxedArrayToPrimitiveArray(parsedValue)
                : parsedValue;

    }

    private static ValueParser<?> getValueParser(ValueParserFactory valueParserFactory,
                                                 Class<?> configClass,
                                                 Method method) {
        try {
            return valueParserFactory.getValueParser(method.getReturnType());
        } catch (UnsupportedTypeException e) {
            throw new UnsupportedTypeException(
                    String.format("%s (%s.%s)",
                            e.getMessage(), configClass.getSimpleName(), method.getName()));
        }
    }


    private static boolean isToStringMethod(Method method) {
        if (toStringMethod == null) {
            try {
                toStringMethod = Object.class.getMethod("toString");
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return method.equals(toStringMethod);
    }

    private static String formatValue(Object value) {
        return value != null && value.getClass().isArray()
                ? formatArray(value)
                : String.valueOf(value);
    }

    private static String formatArray(Object array) {
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(Array.get(array, i));
        }
        buf.append(']');
        return buf.toString();
    }

    private static Method toStringMethod;

    private final Map<Method, Object> values;

    private ProxyInvocationHandler(Map<Method, Object> values) {
        this.values = values;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        return isToStringMethod(method)
                ? toString()
                : values.get(method);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append('{');
        for (Method method : values.keySet()) {
            if (buf.length() > 1) {
                buf.append(", ");
            }
            buf.append(method.getName())
                    .append('=')
                    .append(formatValue(values.get(method)));
        }
        buf.append('}');
        return buf.toString();
    }
}
