package com.statemachinesystems.envy;

import com.statemachinesystems.envy.values.ConfigMap;
import com.statemachinesystems.envy.values.ConfigValue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

    private static boolean hasDefaultAnnotation(Method method) {
        return method.getAnnotation(Default.class) != null;
    }

    private static boolean isMandatory(Method method) {
        @SuppressWarnings("deprecation")
        boolean notAnnotated = method.getAnnotation(Nullable.class) == null
                && method.getAnnotation(Optional.class) == null;

        return (method.getReturnType().isPrimitive() || notAnnotated)
                && ! OptionalWrapper.isWrapperType(method.getReturnType())
                && ! KotlinDefaultMethodInvoker.hasDefaultImplementation(method);
    }

    private static Collection<Method> getMethods(Class<?> configClass) {
        return getMethodsByName(configClass).values();
    }

    private static Map<String, Method> getMethodsByName(Class<?> configClass) {
        Map<String, Method> methodsByName = new TreeMap<>();

        for (Class<?> superInterface : configClass.getInterfaces()) {
            Map<String, Method> superMethodsByName = getMethodsByName(superInterface);
            for (String superMethodName : superMethodsByName.keySet()) {
                if (! methodsByName.containsKey(superMethodName)) {
                    methodsByName.put(superMethodName, superMethodsByName.get(superMethodName));
                }
            }
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

    private ConfigValue extractValue(Class<?> configClass, Class<?> propertyClass, Method method, Parameter parameter) {
        OptionalWrapper wrapper = OptionalWrapper.wrapperOrNull(propertyClass, method);
        if (wrapper != null) {
            ConfigValue value = extractValue(configClass, wrapper.getPropertyClass(), method, parameter);
            return new ConfigValue(wrapper.wrap(value.getValue()), value.getStatus());
            //return value instanceof KotlinDefaultMethodInvoker ? value : wrapper.wrap(value);
        }

        ValueParser<?> valueParser = valueParserFactory.getValueParser(propertyClass);

        if (valueParser != null) {
            ConfigValue rawValue = getRawValue(parameter, configClass, method);
            String rawStringValue = (String) rawValue.getValue();
            Object parsedValue = parseValue(valueParser, rawStringValue, propertyClass);
            return new ConfigValue(parsedValue, rawValue.getStatus());
            //return parsedValue != null ? parsedValue : KotlinDefaultMethodInvoker.invokerOrNull(method);
        } else if (propertyClass.isInterface()) {
            return extractNestedValue(method, parameter, propertyClass);
        } else {
            throw new UnsupportedTypeException(
                    String.format("Cannot parse value of class %s (%s.%s)",
                            propertyClass.getName(), configClass.getSimpleName(), method.getName()));
        }
    }

    private ConfigValue getRawValue(Parameter parameter, Class<?> configClass, Method method) {
        String rawValue = configSource.getValue(parameter);

        ConfigValue.Status status;
        if (rawValue != null) {
            status = ConfigValue.Status.CONFIGURED;
        } else {
            rawValue = getDefaultValue(method);
            if (rawValue != null) {
                status = ConfigValue.Status.DEFAULTED;
            } else {
                status = ConfigValue.Status.MISSING;
            }
        }

        if (rawValue == null && isMandatory(method)) {
            throw new MissingParameterValueException(configClass, method, parameter);
        }
        return new ConfigValue(rawValue, status);
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

    private ConfigValue extractNestedValue(Method method, Parameter parameter, Class<?> propertyClass) {
        if (hasDefaultAnnotation(method)) {
            throw new IllegalArgumentException("Default values are not applicable to nested configuration");
        }
        try {
            // TODO if !configMap.isFullyConfigured() check for default method first
            ConfigMap configMap = extractConfigMap(propertyClass, parameter);
            Object proxy = ProxyInvocationHandler.proxy(propertyClass, configMap);
            return new ConfigValue(proxy, ConfigValue.Status.CONFIGURED);
        } catch (MissingParameterValueException missingParameterValue) {
            if (isMandatory(method)) {
                throw missingParameterValue;
            } else {
                return new ConfigValue(null, ConfigValue.Status.MISSING);
                //return KotlinDefaultMethodInvoker.invokerOrNull(method);
            }
        }
    }
}
