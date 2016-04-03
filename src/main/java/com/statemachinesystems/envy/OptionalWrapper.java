package com.statemachinesystems.envy;

import java.lang.reflect.*;

public class OptionalWrapper {

    private static class Invoker {
        private final String className;
        private final String methodName;

        public Invoker(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }

        private boolean supports(Class<?> c) {
            return className.equals(c.getName());
        }

        public Object create(Object object) throws Exception {
            return Class.forName(className)
                    .getMethod(methodName, Object.class)
                    .invoke(null, object);
        }
    }

    private static final Invoker[] INVOKERS = {
            new Invoker("java.util.Optional", "ofNullable"),
            new Invoker("scala.Option", "apply"),
            new Invoker("com.google.common.base.Optional", "fromNullable")
    };

    public static OptionalWrapper wrapperOrNull(Class<?> propertyClass, Method method) {
        Invoker invoker = invokerOrNull(propertyClass);
        return invoker != null
                ? new OptionalWrapper(invoker, extractPropertyClass(method.getGenericReturnType()))
                : null;
    }

    public static boolean isWrapperType(Class<?> propertyClass) {
        return invokerOrNull(propertyClass) != null;
    }

    private static Invoker invokerOrNull(Class<?> propertyClass) {
        for (Invoker invoker : INVOKERS) {
            if (invoker.supports(propertyClass)) {
                return invoker;
            }
        }
        return null;
    }

    private static Class<?> extractPropertyClass(Type type) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type propertyType = parameterizedType.getActualTypeArguments()[0];

        if (propertyType instanceof Class) {
            return (Class<?>) propertyType;
        }

        if (propertyType instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) propertyType;
            Type componentType = arrayType.getGenericComponentType();
            if (componentType instanceof Class) {
                return Array.newInstance((Class<?>) componentType, 0).getClass();
            }
        }

        throw new UnsupportedTypeException("Unsupported generic type " + type);
    }

    private final Invoker invoker;
    private final Class<?> propertyClass;

    public OptionalWrapper(Invoker invoker, Class<?> propertyClass) {
        this.invoker = invoker;
        this.propertyClass = propertyClass;
    }

    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    public Object wrap(Object object) {
        try {
            return invoker.create(object);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
