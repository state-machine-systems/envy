package com.statemachinesystems.envy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class KotlinDefaultMethodInvoker {

    public static boolean hasDefaultImplementation(Method method) {
        return invokerOrNull(method) != null;
    }

    public static KotlinDefaultMethodInvoker invokerOrNull(Method method) {
        Class<?> configClass = method.getDeclaringClass();

        Method[] defaultImplsMethods;
        try {
            Class<?> defaultImplsClass = Class.forName(configClass.getName() + "$DefaultImpls");
            defaultImplsMethods = defaultImplsClass.getDeclaredMethods();
        } catch (ClassNotFoundException ignored) {
            return null;
        }

        for (Method defaultMethod : defaultImplsMethods) {
            if (Modifier.isStatic(defaultMethod.getModifiers())
                    && defaultMethod.getName().equals(method.getName())
                    && defaultMethod.getReturnType().equals(method.getReturnType())
                    && defaultMethod.getParameterTypes().length == 1
                    && defaultMethod.getParameterTypes()[0].equals(configClass)) {
                return new KotlinDefaultMethodInvoker(defaultMethod);
            }
        }

        return null;
    }

    private final Method defaultMethod;

    private KotlinDefaultMethodInvoker(Method defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    public Object invoke(Object proxy) {
        try {
            return defaultMethod.invoke(null, proxy);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalStateException(
                    "Unable to call Kotlin default method implementation " + defaultMethod.getName(), ex);
        }
    }
}
