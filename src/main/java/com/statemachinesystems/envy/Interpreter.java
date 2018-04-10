package com.statemachinesystems.envy;

import com.statemachinesystems.envy.model.GroupProperty;
import com.statemachinesystems.envy.model.Property;
import com.statemachinesystems.envy.model.ScalarProperty;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static com.statemachinesystems.envy.Assertions.assertMethodWithNoParameters;
import static com.statemachinesystems.envy.Assertions.assertMethodWithNonVoidReturnType;
import static com.statemachinesystems.envy.Assertions.assertNotObjectMethod;

public class Interpreter {

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
        Map<String, Method> methodsByName = new TreeMap<>();
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

    public Interpreter(ValueParserFactory valueParserFactory) {
        this.valueParserFactory = valueParserFactory;
    }

    public <T> GroupProperty<T> interpret(Class<T> configClass) {
        return interpret(configClass, getPrefix(configClass));
    }

    private <T> GroupProperty<T> interpret(Class<T> configClass, Parameter prefix) {
        Map<String, Property<?>> properties = new TreeMap<>();

        for (Method method : getMethods(configClass)) {
            assertMethodWithNoParameters(method);
            assertNotObjectMethod(method);
            assertMethodWithNonVoidReturnType(method);

            Parameter parameter = getParameter(method, prefix);
            Property property = getProperty(configClass, method.getReturnType(), method, parameter);
            properties.put(method.getName(), property);
        }

        return new GroupProperty<T>(prefix, configClass, Collections.unmodifiableMap(properties));
    }

    private Property<?> getProperty(Class<?> configClass, Class<?> propertyClass, Method method, Parameter parameter) {
//        OptionalWrapper wrapper = OptionalWrapper.wrapperOrNull(propertyClass, method);
//        if (wrapper != null) {
//            return wrapper.wrap(extractValue(configClass, wrapper.getPropertyClass(), method, parameter));
//        }

        ValueParser<?> valueParser = valueParserFactory.getValueParser(propertyClass);

        if (valueParser != null) {
            return new ScalarProperty<>(parameter, valueParser);
        } else if (propertyClass.isInterface()) {
            return interpret(propertyClass, parameter);
        } else {
            throw new UnsupportedTypeException(
                    String.format("Cannot interpret value of class %s (%s.%s)",
                            propertyClass.getName(), configClass.getSimpleName(), method.getName()));
        }
    }
}
