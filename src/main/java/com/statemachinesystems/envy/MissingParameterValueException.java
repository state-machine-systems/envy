package com.statemachinesystems.envy;

import java.lang.reflect.Method;

/**
 * {@link java.lang.RuntimeException} thrown when a value is missing for a mandatory configuration parameter.
 */
public class MissingParameterValueException extends RuntimeException  {

    public MissingParameterValueException(Class<?> configClass, Method method, Parameter parameter) {
        super(String.format("Missing configuration parameter value for %s in %s.%s",
                parameter, configClass.getSimpleName(), method.getName()));
    }
}
