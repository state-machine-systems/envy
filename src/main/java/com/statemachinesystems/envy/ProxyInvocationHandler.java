package com.statemachinesystems.envy;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.statemachinesystems.envy.Assertions.*;

/**
 * Handles method calls on proxied configuration interfaces.
 *
 * Method names and types on the interface are mapped to {@link com.statemachinesystems.envy.Parameter}s
 * and {@link com.statemachinesystems.envy.ValueParser}s.
 *
 * @see java.lang.reflect.InvocationHandler
 */
public class ProxyInvocationHandler implements InvocationHandler, Serializable {

    public static ProxyInvocationHandler createInvocationHandler(
            Class<?> configClass,
            ConfigSource configSource,
            ValueParserFactory valueParserFactory) {

        Map<String, Object> values = new LinkedHashMap<String, Object>();

        for (Method method : configClass.getDeclaredMethods()) {
            assertMethodWithNoParameters(method);
            assertNotObjectMethod(method);
            assertMethodWithNonVoidReturnType(method);

            Parameter parameter = getParameter(method);
            String rawValue = getRawValue(configSource, parameter, configClass, method);
            Object parsedValue = parseValue(rawValue, valueParserFactory, configClass, method);

            values.put(method.getName(), parsedValue);
        }

        return new ProxyInvocationHandler(configClass, values);
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

    private static Method getObjectMethod(String name, Class<?>... argumentTypes) {
        try {
            return Object.class.getMethod(name, argumentTypes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
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

    private static final Method TO_STRING_METHOD = getObjectMethod("toString");
    private static final Method EQUALS_METHOD = getObjectMethod("equals", Object.class);
    private static final Method HASH_CODE_METHOD = getObjectMethod("hashCode");

    private final Class<?> configClass;
    private final Map<String, Object> values;

    private ProxyInvocationHandler(Class<?> configClass, Map<String, Object> values) {
        this.configClass = configClass;
        this.values = values;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object value = values.get(method.getName());

        if (value == null) {
            if (TO_STRING_METHOD.equals(method)) {
                return toString();
            } else if (EQUALS_METHOD.equals(method)) {
                return proxyEquals(proxy, args[0]);
            } else if (HASH_CODE_METHOD.equals(method)) {
                return proxyHashCode();
            }
        }

        return value;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append('{');
        for (String methodName : values.keySet()) {
            if (buf.length() > 1) {
                buf.append(", ");
            }
            buf.append(methodName)
                    .append('=')
                    .append(formatValue(values.get(methodName)));
        }
        buf.append('}');
        return buf.toString();
    }

    private boolean proxyEquals(Object proxy, Object other) {
        if (other == null) {
            return false;
        }

        InvocationHandler otherHandler;
        try {
            otherHandler = Proxy.getInvocationHandler(other);
        } catch (Exception ignored) {
            return false;
        }

        if (!(otherHandler instanceof ProxyInvocationHandler)) {
            return false;
        }

        ProxyInvocationHandler otherEnvyHandler = (ProxyInvocationHandler) otherHandler;

        return configClass.equals(otherEnvyHandler.configClass)
                && values.equals(otherEnvyHandler.values);
    }

    private int proxyHashCode() {
        return values.hashCode();
    }
}
