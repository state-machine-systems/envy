package com.statemachinesystems.envy.model;

import com.statemachinesystems.envy.*;

import java.lang.reflect.Method;
import java.util.Objects;

public class ScalarProperty<T> implements Property<T> {

//    private final Class<?> configClass;
//    private final Method method;
//    private final Class<?> propertyClass;
    private final Parameter parameter;
    private final ValueParser<T> valueParser;
    private final boolean isMandatory;
    private final String defaultValue;

    public ScalarProperty(Parameter parameter, ValueParser<T> valueParser, boolean isMandatory, String defaultValue) {
        Objects.requireNonNull(parameter);
        Objects.requireNonNull(valueParser);

        this.parameter = parameter;
        this.valueParser = valueParser;
        this.isMandatory = isMandatory;
        this.defaultValue = defaultValue;
    }

    public T extract(ConfigSource configSource) {
        throw new RuntimeException("lol");
    }
}
