package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.Conversions;
import com.statemachinesystems.envy.ValueParser;
import sun.reflect.ConstantPool;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

public class FunctionValueParser<T> implements ValueParser<T> {

    private final Class<T> valueClass;
    private final Function<String, T> function;

    public FunctionValueParser(Class<T> valueClass, Function<String, T> function) {
        this.valueClass = valueClass;
        this.function = function;
    }

    public FunctionValueParser(Function<String, T> function) {
        this(getReturnType(function), function);
    }

    @Override
    public T parseValue(String value) {
        return function.apply(value);
    }


    @Override
    public Class<T> getValueClass() {
        return valueClass;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getReturnType(Function<String, T> function) {
        Class<?> functionClass = function.getClass();

        Class<?> returnType = getReturnTypeOfApplyMethod(functionClass);
        if (returnType == null) {
            returnType = getReturnTypeOfLambda(functionClass);
        }
        if (returnType == null) {
            throw new IllegalStateException("Couldn't get return type from function " + function);
        }

        return (Class<T>) Conversions.toBoxed(returnType);
    }

    private static Class<?> getReturnTypeOfApplyMethod(Class<?> functionClass) {
        try {
            Method applyMethod = functionClass.getMethod("apply", String.class);
            return applyMethod.getReturnType();
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private static Class<?> getReturnTypeOfLambda(Class<?> functionClass) {
        ConstantPool constantPool = sun.misc.SharedSecrets.getJavaLangAccess().getConstantPool(functionClass);
        for (int i = 0; i < constantPool.getSize(); i++) {
            try {
                Object entry = constantPool.getMethodAt(i);
                if (entry instanceof Method) {
                    return ((Method) entry).getReturnType();
                }
            } catch (Exception ignored) {}
        }
        return null;
    }
}
