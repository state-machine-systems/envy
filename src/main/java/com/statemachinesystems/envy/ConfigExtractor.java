package com.statemachinesystems.envy;

import java.lang.reflect.Method;
import java.util.*;

import static com.statemachinesystems.envy.Assertions.*;

/**
 * Extracts configuration values indexed by method name, as used by
 * {@link com.statemachinesystems.envy.ProxyInvocationHandler}.
 *
 * Values are populated using a given {@link com.statemachinesystems.envy.ConfigSource} and
 * {@link com.statemachinesystems.envy.ValueParserFactory}.
 */
public class ConfigExtractor {

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

    private static Collection<Method> getMethods(Class<?> configClass) {
        return getMethodsByName(configClass).values();
    }

    private static Map<String, Method> getMethodsByName(Class<?> configClass) {
        Map<String, Method> methodsByName = new LinkedHashMap<String, Method>();
        for (Class<?> superInterface : configClass.getInterfaces()) {
            methodsByName.putAll(getMethodsByName(superInterface));
        }
        for (Method method : configClass.getDeclaredMethods()) {
            methodsByName.put(method.getName(), method);
        }
        return methodsByName;
    }

    private static Parameter getParameter(Method method, Parameter prefix) {
        Name customParameterName = method.getAnnotation(Name.class);
        Parameter parameter = customParameterName != null
                ? new Parameter(customParameterName.value())
                : Parameter.fromMethodName(method.getName());

        return prefix != null
                ? prefix.join(parameter)
                : parameter;
    }

    private static Parameter getPrefix(Class<?> configClass) {
        Prefix prefix = configClass.getAnnotation(Prefix.class);
        return prefix != null ? new Parameter(prefix.value()) : null;
    }

    private final ValueParserFactory valueParserFactory;
    private final ConfigSource configSource;

    /**
     * Creates a new {@link com.statemachinesystems.envy.ConfigExtractor} instance with the given
     * {@link com.statemachinesystems.envy.ValueParserFactory} and {@link com.statemachinesystems.envy.ConfigSource}.
     *
     * @param valueParserFactory  the {@link com.statemachinesystems.envy.ValueParserFactory} to use
     * @param configSource        the {@link com.statemachinesystems.envy.ConfigSource} to use
     */
    public ConfigExtractor(ValueParserFactory valueParserFactory, ConfigSource configSource) {
        this.valueParserFactory = valueParserFactory;
        this.configSource = configSource;
    }

    /**
     * Extracts configuration values indexed by method name, as used by
     * {@link com.statemachinesystems.envy.ProxyInvocationHandler}.
     *
     * @param configClass  the class of the configuration interface
     * @return  a populated map of method names to configuration values
     */
    public Map<String, Object> extractValuesByMethodName(Class<?> configClass) {

        Map<String, Object> values = new LinkedHashMap<String, Object>();

        Parameter prefix = getPrefix(configClass);

        for (Method method : getMethods(configClass)) {
            assertMethodWithNoParameters(method);
            assertNotObjectMethod(method);
            assertMethodWithNonVoidReturnType(method);

            Parameter parameter = getParameter(method, prefix);
            String rawValue = getRawValue(parameter, configClass, method);
            Object parsedValue = parseValue(rawValue, configClass, method);

            values.put(method.getName(), parsedValue);
        }

        return Collections.unmodifiableMap(values);
    }

    private String getRawValue(Parameter parameter, Class<?> configClass, Method method) {

        String rawValue = configSource.getValue(parameter);
        if (rawValue == null) {
            rawValue = getDefaultValue(method);
        }
        if (rawValue == null && isMandatory(method)) {
            throw new IllegalArgumentException(
                    String.format("Missing configuration value for %s.%s/%s",
                            configClass.getSimpleName(), method.getName(), parameter));
        }
        return rawValue;
    }

    private Object parseValue(String rawValue, Class<?> configClass, Method method) {
        if (rawValue == null) {
            return null;
        }

        ValueParser<?> valueParser = getValueParser(configClass, method);
        Object parsedValue = valueParser.parseValue(rawValue);

        return Conversions.isPrimitiveArray(method.getReturnType())
                ? Conversions.boxedArrayToPrimitiveArray(parsedValue)
                : parsedValue;

    }

    private ValueParser<?> getValueParser(Class<?> configClass, Method method) {
        try {
            return valueParserFactory.getValueParser(method.getReturnType());
        } catch (UnsupportedTypeException e) {
            throw new UnsupportedTypeException(
                    String.format("%s (%s.%s)",
                            e.getMessage(), configClass.getSimpleName(), method.getName()));
        }
    }
}
