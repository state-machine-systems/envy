package com.statemachinesystems.envy;

import com.statemachinesystems.envy.values.ConfigMap;
import com.statemachinesystems.envy.values.ConfigValue;
import com.statemachinesystems.envy.values.StaticConfigValue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

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
        @SuppressWarnings("deprecation")
        boolean notAnnotated = method.getAnnotation(Nullable.class) == null
                && method.getAnnotation(Optional.class) == null;

        return (method.getReturnType().isPrimitive() || notAnnotated)
                && ! OptionalWrapper.isWrapperType(method.getReturnType());
    }

    private static Collection<Method> getMethods(Class<?> configClass) {
        return getMethodsByName(configClass).values();
    }

    private static Map<String, Method> getMethodsByName(Class<?> configClass) {
        Map<String, Method> methodsByName = new TreeMap<String, Method>();
        for (Class<?> superInterface : configClass.getInterfaces()) {
            methodsByName.putAll(getMethodsByName(superInterface));
        }
        for (Method method : configClass.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                methodsByName.put(method.getName(), method);
            }
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
     * Extracts a {@link ConfigMap} of configuration values indexed by method name, as used by
     * {@link com.statemachinesystems.envy.ProxyInvocationHandler}.
     *
     * @param configClass  the class of the configuration interface
     * @return  a populated {@link ConfigMap} of method names to configuration values
     */
    public ConfigMap extractConfigMap(Class<?> configClass) {
        return extractConfigMap(configClass, getPrefix(configClass));
    }

    private ConfigMap extractConfigMap(Class<?> configClass, Parameter prefix) {
        Map<String, ConfigValue> values = new TreeMap<>();

        for (Method method : getMethods(configClass)) {
            assertMethodWithNoParameters(method);
            assertNotObjectMethod(method);
            assertMethodWithNonVoidReturnType(method);

            Parameter parameter = getParameter(method, prefix);
            ConfigValue value = extractValue(configClass, method.getReturnType(), method, parameter);
            values.put(method.getName(), value);
        }

        return new ConfigMap(Collections.unmodifiableMap(values));
    }

    private <T> ConfigValue<T> extractValue(Class<?> configClass, Class<T> propertyClass, Method method, Parameter parameter) {
        OptionalWrapper<T> wrapper = OptionalWrapper.wrapperOrNull(propertyClass, method);
        if (wrapper != null) {
            ConfigValue<?> value = extractValue(configClass, wrapper.getPropertyClass(), method, parameter);
            return new StaticConfigValue<>(wrapper.wrap(value.getValue(null)), value.getStatus());
        }

        ValueParser<T> valueParser = valueParserFactory.getValueParser(propertyClass);

        if (valueParser != null) {
            ConfigValue<String> rawValue = getRawValue(parameter, configClass, method);
            String rawStringValue = rawValue.getValue(null);
            T parsedValue = parseValue(valueParser, rawStringValue, propertyClass);
            return new StaticConfigValue<>(parsedValue, rawValue.getStatus());
        } else if (propertyClass.isInterface()) {
            return extractNestedValue(method, parameter, propertyClass);
        } else {
            throw new UnsupportedTypeException(
                    String.format("Cannot parse value of class %s (%s.%s)",
                            propertyClass.getName(), configClass.getSimpleName(), method.getName()));
        }
    }

    private ConfigValue<String> getRawValue(Parameter parameter, Class<?> configClass, Method method) {
        String rawValue = configSource.getValue(parameter);

        ConfigValue.Status status;
        if (rawValue != null) {
            status = ConfigValue.Status.CONFIGURED;
        } else {
            rawValue = getDefaultValue(method);
            status = rawValue != null ? ConfigValue.Status.DEFAULTED : ConfigValue.Status.MISSING;
        }

        if (rawValue == null && isMandatory(method)) {
            throw new MissingParameterValueException(configClass, method, parameter);
        }
        return new StaticConfigValue<>(rawValue, status);
    }

    private <T> T parseValue(ValueParser<T> valueParser, String rawValue, Class<T> propertyClass) {
        if (rawValue == null) {
            return null;
        }

        T parsedValue = valueParser.parseValue(rawValue);

        return Conversions.isPrimitiveArray(propertyClass)
                ? Conversions.boxedArrayToPrimitiveArray(parsedValue)
                : parsedValue;
    }

    private <T> ConfigValue<T> extractNestedValue(Method method, Parameter parameter, Class<T> propertyClass) {
        if (hasDefaultAnnotation(method)) {
            throw new IllegalArgumentException("Default values are not applicable to nested configuration");
        }
        try {
            T proxy = ProxyInvocationHandler.proxy(propertyClass, extractConfigMap(propertyClass, parameter));
            return new StaticConfigValue<>(proxy, ConfigValue.Status.CONFIGURED);
        } catch (MissingParameterValueException missingParameterValue) {
            if (isMandatory(method)) {
                throw missingParameterValue;
            } else {
                return new StaticConfigValue<>(null, StaticConfigValue.Status.MISSING);
            }
        }
    }
}
