package com.statemachinesystems.envy;

import com.statemachinesystems.envy.values.ConfigMap;
import com.statemachinesystems.envy.values.ConfigValue;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static com.statemachinesystems.envy.Assertions.assertInterface;

/**
 * Handles method calls on proxied configuration interfaces.
 *
 * @see java.lang.reflect.InvocationHandler
 */
public class ProxyInvocationHandler implements InvocationHandler, Serializable {

    /**
     * Builds a proxy configuration object from the given interface and configuration values.
     *
     * @param configClass  the configuration interface to be proxied
     * @param values       map of configuration values indexed by method name
     * @param <T>          the type of the configuration interface
     * @return             a configuration object that implements the interface
     */
    public static <T> T proxy(Class<T> configClass, ConfigMap values) {
        assertInterface(configClass);

        InvocationHandler invocationHandler = new ProxyInvocationHandler(configClass, values);

        ClassLoader classLoader = configClass.getClassLoader();
        Class<?>[] proxyInterfaces = new Class<?>[] { configClass };

        @SuppressWarnings("unchecked")
        T proxyInstance = (T) Proxy.newProxyInstance(classLoader, proxyInterfaces, invocationHandler);

        return proxyInstance;
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
    private final ConfigMap values;

    /**
     * Creates a {@link com.statemachinesystems.envy.ProxyInvocationHandler} instance using the given interface
     * and configuration values.
     *
     * @param configClass  the configuration interface to be proxied
     * @param values       map of configuration values indexed by method name
     */
    private ProxyInvocationHandler(Class<?> configClass, ConfigMap values) {
        this.configClass = configClass;
        this.values = values;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        ConfigValue value = values.getValue(method.getName());

        if (value == null) {
            if (TO_STRING_METHOD.equals(method)) {
                return toString();
            } else if (EQUALS_METHOD.equals(method)) {
                return proxyEquals(args[0]);
            } else if (HASH_CODE_METHOD.equals(method)) {
                return proxyHashCode();
            } else {
                throw new IllegalStateException("Missing method implementation: " + method);
            }
        }
/*
        if (value instanceof KotlinDefaultMethodInvoker) {
            return ((KotlinDefaultMethodInvoker) value).invoke(proxy);
        }
 */
        return value.getValue();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append('{');

        boolean first = true;
        for (String methodName : values.getMethodNames()) {
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append(methodName)
                    .append('=')
                    .append(formatValue(values.getValue(methodName).getValue()));
        }

        buf.append('}');
        return buf.toString();
    }

    private boolean proxyEquals(Object other) {
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
