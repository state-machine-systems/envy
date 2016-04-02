package com.statemachinesystems.envy;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private static boolean hasDefaultAnnotation(Method method) {
        return method.getAnnotation(Default.class) != null;
    }

    private static boolean isMandatory(Method method) {
        return (method.getReturnType().isPrimitive() || method.getAnnotation(Optional.class) == null)
                && ! OptionalWrapper.isWrapperType(method.getReturnType());
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
        return extractValuesByMethodName(configClass, getPrefix(configClass));
    }

    private Map<String, Object> extractValuesByMethodName(Class<?> configClass, Parameter prefix) {
        Map<String, Object> values = new LinkedHashMap<String, Object>();

        for (Method method : getMethods(configClass)) {
            assertMethodWithNoParameters(method);
            assertNotObjectMethod(method);
            assertMethodWithNonVoidReturnType(method);

            Parameter parameter = getParameter(method, prefix);
            Object value = extractValue(configClass, method.getReturnType(), method, parameter);
            values.put(method.getName(), value);
        }

        return Collections.unmodifiableMap(values);
    }

    private Object extractValue(Class<?> configClass, Class<?> propertyClass, Method method, Parameter parameter) {
        OptionalWrapper wrapper = OptionalWrapper.wrapperOrNull(propertyClass, method);
        if (wrapper != null) {
            return wrapper.wrap(extractValue(configClass, wrapper.getPropertyClass(), method, parameter));
        }

        ValueParser<?> valueParser = valueParserFactory.getValueParser(propertyClass);

        if (valueParser != null) {
            String rawValue = getRawValue(parameter, configClass, method);
            return parseValue(valueParser, rawValue, propertyClass);
        } else if (propertyClass.isInterface()) {
            return extractNestedValue(method, parameter, propertyClass);
        } else {
            throw new UnsupportedTypeException(
                    String.format("Cannot parse value of class %s (%s.%s)",
                            propertyClass.getName(), configClass.getSimpleName(), method.getName()));
        }
    }

    private String getRawValue(Parameter parameter, Class<?> configClass, Method method) {

        String rawValue = configSource.getValue(parameter);
        if (rawValue == null) {
            rawValue = getDefaultValue(method);
        }
        if (rawValue == null && isMandatory(method)) {
            throw new MissingParameterValueException(configClass, method, parameter);
        }
        return rawValue;
    }

    private Object parseValue(ValueParser<?> valueParser, String rawValue, Class<?> propertyClass) {
        if (rawValue == null) {
            return null;
        }

        Object parsedValue = valueParser.parseValue(rawValue);

        return Conversions.isPrimitiveArray(propertyClass)
                ? Conversions.boxedArrayToPrimitiveArray(parsedValue)
                : parsedValue;
    }

    private Object extractNestedValue(Method method, Parameter parameter, Class<?> propertyClass) {
        if (hasDefaultAnnotation(method)) {
            throw new IllegalArgumentException("Default values are not applicable to nested configuration");
        }
        try {
            return ProxyInvocationHandler.proxy(propertyClass, extractValuesByMethodName(propertyClass, parameter));
        } catch (MissingParameterValueException missingParameterValue) {
            if (isMandatory(method)) {
                throw missingParameterValue;
            } else {
                return null;
            }
        }
    }
}
